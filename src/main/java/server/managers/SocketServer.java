    package server.managers;

    import global.facility.Request;
    import global.facility.Response;
    import global.facility.Route;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import server.rulers.CommandManager;

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

    public class SocketServer {
        private static final Logger log = LoggerFactory.getLogger(SocketServer.class);
        private final CommandManager commandRuler;
        private Selector selector;
        private InetSocketAddress address;
        private Set<SocketChannel> session;

        public SocketServer(String host, int port, CommandManager commandRuler) {
            this.address = new InetSocketAddress(host, port);
            this.session = new HashSet<>();
            this.commandRuler=commandRuler;
        }

        public void start() throws IOException, ClassNotFoundException {
            this.selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(address);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

            log.info("Server started...");
            new Thread(() -> {
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                while (true) {
                    try {

                        String input = consoleReader.readLine();
                        String[] tokens = (input.trim() + " ").split(" ", 2);
                        tokens[1] = tokens[1].trim();
                        String executingCommand = tokens[0];
                        var command = commandRuler.getCommands().get("save");
                        var exitCommand = commandRuler.getCommands().get("exit");
                        if (executingCommand.equals("save")) {
                            Response serverResponse = command.apply(tokens,null);
                        }else{
                            if(executingCommand.equals("exit")){
                                Response serverResponseSave = command.apply(tokens, null);
                                Response serverResponseExit = exitCommand.apply(tokens , null);
                            }else{
                                log.warn("Внимание! Введенная вами команда отсутствует в базе сервера. Вам доступны следующие две команы : save , exit. Введите любую из них.");
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
            channel.register(this.selector, SelectionKey.OP_READ);
            this.session.add(channel);
            log.info("Подключился новый пользователь: " + channel.socket().getRemoteSocketAddress() + "\n");
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
                    log.info("Пользователь отключился: " + channel.socket().getRemoteSocketAddress() + "\n");
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
                    log.info("Получено: " + gotData + " | Route:" + gotRoute);

                    String[] tokens = (gotData.trim() + " ").split(" ", 2);
                    tokens[1] = tokens[1].trim();
                    String executingCommand = tokens[0];
                    commandRuler.addToHistory(executingCommand);
                    var command = commandRuler.getCommands().get(executingCommand);
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
                    log.error("Ошибка обработки запроса: " + e.getMessage());
                } catch (EOFException | StreamCorruptedException e) {
                    // Не удалось десериализовать объект, возможно, не все данные получены
                    log.error("Получены неполные данные.");
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