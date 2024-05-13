package test;

import client.managers.Client;

public class Main {
    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost",8080);
        client.start();
    }
}