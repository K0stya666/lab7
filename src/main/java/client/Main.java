package client;

import client.managers.Client;

public class Main {
    private static final String HOST = "localhost";
    private static final int PORT = 4093;

    public static void main(String[] args) {
        try {
            Client client = new Client(HOST, PORT);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Ошибка при запуске клиента: " + e.getMessage());
        }
    }
}
