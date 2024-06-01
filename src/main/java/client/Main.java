package client;

import client.managers.Client;
import client.tools.Ask;

import java.io.IOException;

public class Main {
    private static final int PORT = 4129;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws Ask.AskBreak, IOException, ClassNotFoundException, InterruptedException {
        Client client = new Client(HOST, PORT);
        client.start();
    }
}
