package client;

import client.managers.SocketClient;

public class Main {
    public static void main(String[] args) throws Exception {


        if (args.length < 2) {
            System.out.println("Usage: java -jar client.jar <host> <port>");
            return;
        }

        String host = args[0];  // Хост берется из первого аргумента
        int port = Integer.parseInt(args[1]);  // Порт берется из второго аргумента и преобразуется в число

        try {
            SocketClient client = new SocketClient(host, port);
            client.start();  // Запускаем клиент
        } catch (Exception e) {
            System.out.println("Ошибка при запуске клиента: " + e.getMessage());
            e.printStackTrace();
        }
    }
//        SocketClient client = new SocketClient("localhost",8080);
//        client.start();
    }
