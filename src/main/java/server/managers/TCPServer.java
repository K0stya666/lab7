package server.managers;

import global.models.*;
import global.models.Route;
import static server.commands.Commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TCPServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TCPServer.class);
    private final CommandManager commandManager;
    private final InetSocketAddress address;
    private static ServerSocketChannel serverChannel;
    private static SocketChannel clientChannel;
    private static Selector selector;
    private final Set<SocketChannel> session;

    public TCPServer(String host, int port, CommandManager commandManager) {
        this.address = new InetSocketAddress(host, port);
        this.session = new HashSet<>();
        this.commandManager = commandManager;
    }

    /**
     * Запускает сервер
     * @throws IOException исключение ввода-вывода
     * @throws ClassNotFoundException исключение не существования класса
     */
    public void start() throws IOException, ClassNotFoundException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(address);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        LOGGER.info("Server started on address: {}", serverChannel.getLocalAddress());
        new Thread(() -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                try {
                    String[] input = br.readLine().trim().split(" ", 2);
                    var request = new Request(input);
                    var commandName = input[0];
                    var command = commandManager.getCommand(commandName);
                    if (EXIT.equals(commandName)) {
                        command.execute(request);
                    } else {
                        LOGGER.warn("Внимание! Введенная вами команда отсутствует в базе сервера. Вам доступны следующие две команды: save , exit. Введите любую из них.");
                    }
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    LOGGER.error("Ошибка обработки команды");
                }
            }
        }).start();

        while(true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keys = selectedKeys.iterator();
            while(keys.hasNext()) {
                SelectionKey key = keys.next();
                keys.remove();
                if (!key.isValid()) continue;
                if (key.isAcceptable()) accept(key);
                else if (key.isReadable()) read(key);
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(key.selector(), SelectionKey.OP_READ);
        session.add(channel);
        LOGGER.info("Подключился новый пользователь: {}\n", channel.socket().getRemoteSocketAddress());
    }

    private void read(SelectionKey key) throws IOException, ClassNotFoundException {
        clientChannel = (SocketChannel) key.channel();
        clientChannel.configureBlocking(false);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clientChannel.close();
            LOGGER.info("Пользователь отключился");
            return;
        }

        while (bytesRead > 0) {
            buffer.flip();
            baos.write(buffer.array(), 0, bytesRead);
            buffer.clear();
            bytesRead = clientChannel.read(buffer);
        }

        byte[] requestData = baos.toByteArray();
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(requestData))) {
            var request = (Request) ois.readObject();
            var commandName = request.getCommandName();
            commandManager.addToHistory(commandName);
            var command = commandManager.getCommand(commandName);

            Response response;
            if (command == null) {
                response = new Response("Команда '" + commandName + "' не найдена. Наберите 'help' для справки\n");
            } else { response = command.execute(request); }

            sendAnswer(response, key);
        } catch (InterruptedException e) {
            LOGGER.error("Ошибка обработки запроса");
        }
    }

    public void sendAnswer(Response response, SelectionKey key) throws IOException {
        clientChannel = (SocketChannel) key.channel();
        clientChannel.configureBlocking(false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();

        byte[] responseData = baos.toByteArray();
        ByteBuffer responseBuffer = ByteBuffer.wrap(responseData);
        clientChannel.write(responseBuffer);
    }
}