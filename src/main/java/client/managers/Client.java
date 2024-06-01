package client.managers;

import global.models.*;
import global.tools.*;
import global.tools.Console;
import server.utility.User;
import server.commands.Commands;
import server.managers.CommandManager;
import static server.commands.Commands.*;
import static client.tools.Ask.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Set;

/**
 * Класс для подключения клиента к серверу
 * @author Kostya666
 */
public class Client {
    private static final Console console = new StandartConsole();
    private static final CommandManager commandManager = new CommandManager();
    private static InetSocketAddress address;
    private static SocketChannel channel = null;
    private static Selector selector;
    private static User user;
    public static boolean isLoggedIn = false;

    public Client(String host, int port) throws IOException {
        address = new InetSocketAddress(host, port);
        selector = Selector.open();
    }

    public void start() throws AskBreak, IOException, ClassNotFoundException, InterruptedException {
        connectToServer();
        clientAuthorization();

        while (console.isCanReadln()) {

            var input = console.readln().split(" ", 2);
            var commandName = input[0];

//            var input = console.readln();
//            var commandName = input;

            var request = new Request(input, user);
            var command = commandManager.getCommand(commandName);

            switch (Commands.valueOf(commandName.toUpperCase())) {
                case EXIT:
                    console.println("Завершение работы");
                    try {
                        channel.close();
                        command.execute(request);
                    } catch (IOException | ClassNotFoundException | InterruptedException e) {
                        console.printError("Ошибка при закрытии соединения");
                    }
                    break;
                case ADD, UPDATE_BY_ID:
                    var route = askRoute();
                    request = new Request(input, route, user);
                    sendRequest(request);
                    break;
                case EXECUTE_SCRIPT:
                    command.execute(request);
                    break;
                default:
                    sendRequest(request);
            }
        }
    }

    private void connectToServer() throws InterruptedException {
        while (!isConnected()) {
            try {
                channel = SocketChannel.open();
                channel.configureBlocking(false);
                channel.connect(address);
                channel.register(selector, SelectionKey.OP_CONNECT);

                while (true) {
                    selector.select();
                    Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                    while (keys.hasNext()) {
                        SelectionKey key = keys.next();
                        keys.remove();
                        if (key.isConnectable()) {
                            channel = (SocketChannel) key.channel();
//                            if (channel.isConnected()) {
//                                key.interestOps(SelectionKey.OP_READ);
//                                console.println("Подключение к серверу установлено");
//                                return;
//                            }
                            if (channel.finishConnect()) {
                                key.interestOps(SelectionKey.OP_READ);
                                console.println("Подключение к серверу установлено");
                                return;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                console.printError("Неудачная попытка подключения <ERROR 666>");
                Thread.sleep(5000);
            }
        }
    }

    private void clientAuthorization() throws IOException, ClassNotFoundException, InterruptedException {
        console.println("Пожалуйста войдите в учётную запись или зарегистрируйтесь");
        while (!isLoggedIn) {
            try {
                var input = console.readln().split(" ", 3);
                var commandName  = input[0];
                var userName = input[1];
                var password = input[2];
                user = new User(userName, password);
                if (!SIGNUP.equals(commandName) && !LOGIN.equals(commandName)) {
                    console.println("Для использования команд вам необходимо авторизоваться");
                    console.println("Используйте команду 'signup' или 'login'");
                } else {
                    var request = new Request(input, user);
                    sendRequest(request);
                    isLoggedIn = true;
                    //console.println("Вы зашли под именем: " + userName);
                }
            } catch (NoSuchAlgorithmException | ClassNotFoundException | InterruptedException e) {
                console.printError("Ошибка авторизации пользователя");
            } catch (IOException e) {
                console.printError("Проблемы с подключением к серверу. Попытка переподключиться...");
                connectToServer();
            }
            console.println(getAnswer());
        }
    }

    public static void sendRequest(Request request) throws ClassNotFoundException, InterruptedException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();

        byte[] requestData = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(requestData);
        channel.write(buffer);
    }

    public Response getAnswer() throws IOException, ClassNotFoundException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        channel.configureBlocking(false);
        //selector.wakeup();
        channel.register(selector, SelectionKey.OP_READ);

        while (true) {
            int readyChannels = selector.select();
            if (readyChannels == 0) continue;

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove();

                if (key.isReadable()) {
                    channel = (SocketChannel) key.channel();
                    int bytesRead = channel.read(buffer);

                    if (bytesRead == -1) {
                        key.cancel();
                        channel.close();
                        return null;
                    }

                    buffer.flip();
                    baos.write(buffer.array(), 0, bytesRead);
                    buffer.clear();

                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
                    return (Response) ois.readObject();
                }
            }
        }
    }

    private boolean isConnected() {
        return channel != null && channel.isConnected();
    }
}
