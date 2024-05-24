    package server.managers;

    import global.models.*;
    import global.models.Route;
    import org.slf4j.*;
    import java.io.*;
    import java.net.InetSocketAddress;
    import java.nio.ByteBuffer;
    import java.nio.channels.*;
    import java.util.*;

    public class TCPServer {
        private static final Logger LOGGER = LoggerFactory.getLogger(TCPServer.class);
        private final CommandManager commandManager;
        private Selector selector;
        private final InetSocketAddress address;
        private final Set<SocketChannel> session;

        public TCPServer(String host, int port, CommandManager commandManager) {
            this.address = new InetSocketAddress(host, port);
            this.session = new HashSet<>();
            this.commandManager = commandManager;
        }

        public void start() throws IOException, ClassNotFoundException {
            this.selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(address);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

            LOGGER.info("Server started...");
            new Thread(() -> {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    try {

                        String input = consoleReader.readLine();
                        String[] tokens = (input.trim() + " ").split(" ", 2);
                        tokens[1] = tokens[1].trim();
                        String executingCommand = tokens[0];
                        var command = commandManager.getCommands().get("save");
                        var exitCommand = commandManager.getCommands().get("exit");
                        if (executingCommand.equals("save")) {
                            Response serverResponse = command.apply(tokens,null);
                        }else{
                            if(executingCommand.equals("exit")){
                                Response serverResponseSave = command.apply(tokens, null);
                                Response serverResponseExit = exitCommand.apply(tokens , null);
                            }else{
                                LOGGER.warn("Внимание! Введенная вами команда отсутствует в базе сервера. Вам доступны следующие две команы : save , exit. Введите любую из них.");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();

            while(true) {
                // blocking, wait for events
                this.selector.select();
                Iterator keys = this.selector.selectedKeys().iterator();
                while(keys.hasNext()) {
                    SelectionKey key = (SelectionKey) keys.next();
                    keys.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) accept(key);
                    else if (key.isReadable()) read(key);
                }
            }
        }

        private void accept(SelectionKey key) throws IOException {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel channel = serverSocketChannel.accept();
            channel.configureBlocking(false);
            channel.register(key.selector(), SelectionKey.OP_READ);
            session.add(channel);
            LOGGER.info("Подключился новый пользователь: {}\n", channel.socket().getRemoteSocketAddress());
        }

        private void read(SelectionKey key) throws IOException {
            SocketChannel channel = (SocketChannel) key.channel();
            channel.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            while (true) {
                int numRead = channel.read(buffer);

                if (numRead == -1) {
                    // Клиент закрыл соединение
                    this.session.remove(channel);
                    LOGGER.info("Пользователь отключился: " + channel.socket().getRemoteSocketAddress() + "\n");
                    key.cancel();
                    return;
                }

                if (numRead == 0) {
                    // Нет данных для чтения
                    break;
                }

                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }

            byte[] data = byteArrayOutputStream.toByteArray();
            if (data.length > 0) {
                try (ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(data))) {
                    Request request = (Request) oi.readObject();
                    String gotData = request.getCommandMassage();
                    Route gotRoute = request.getRoute();
                    LOGGER.info("Получено: " + gotData + " | Route:" + gotRoute);

                    String[] tokens = (gotData.trim() + " ").split(" ", 2);
                    tokens[1] = tokens[1].trim();
                    String executingCommand = tokens[0];
                    commandManager.addToHistory(executingCommand);
                    var command = commandManager.getCommands().get(executingCommand);
//                    if (executingCommand.equals("reconect")){
//                        return;
//                    }
                    if (command == null&&!executingCommand.equals("execute_script") ) {
                        sendAnswer(new Response("Команда '" + tokens[0] + "' не найдена. Наберите 'help' для справки\n"), key);
                        return;
                    }

                    Response response = command.apply(tokens , gotRoute);
                    sendAnswer(response, key);
                } catch (ClassNotFoundException e) {
                    LOGGER.error("Ошибка обработки запроса: " + e.getMessage());
                } catch (EOFException | StreamCorruptedException e) {
                    // Не удалось десериализовать объект, возможно, не все данные получены
                    LOGGER.error("Получены неполные данные.");
                }
            }
        }



        public void sendAnswer(Response response, SelectionKey key) throws IOException {
            SocketChannel client = (SocketChannel) key.channel();
            client.configureBlocking(false);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(response);
            objectOutputStream.close();
            ByteBuffer buffer = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
            while(buffer.hasRemaining()){
                client.write(buffer);
            }
        }

    }