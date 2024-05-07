package test;

import client.managers.SocketClient;

public class Main {
    public static void main(String[] args) throws Exception {
        SocketClient client = new SocketClient("localhost",8080);
        client.start();
    }
}