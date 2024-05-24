package server;

import server.commands.*;
//import server.interstates.UserInterstate60;
import server.managers.Interstate60;
import server.managers.TCPServer;
import server.managers.CollectionManager;
import server.managers.CommandManager;

import java.io.IOException;

import static server.managers.Interstate60.createDatabaseIfNotExists;


public class Main {
    private final static int PORT = 4093;

    public static void main(String[] args) {

        createDatabaseIfNotExists();

        var commandManager = new CommandManager();
        var interstate60 = new Interstate60();
        var collectionManager = new CollectionManager(interstate60);

        if (!collectionManager.loadCollection()) {
            System.err.println("Error: Collection could not be loaded!");
            return;
        }

        commandManager.register("add", new Add(collectionManager));
        commandManager.register("clear", new Clear(collectionManager));
        commandManager.register("show", new Show(collectionManager));
        commandManager.register("help", new Help(commandManager));
        commandManager.register("update_by_id", new UpdateById(collectionManager));
        commandManager.register("history", new History(commandManager));
        commandManager.register("exit", new Exit());
        commandManager.register("info", new Info(collectionManager));
        commandManager.register("remove_head", new RemoveHead(collectionManager));
        commandManager.register("print_ascending", new PrintAscending(collectionManager));
        commandManager.register("remove_by_id", new RemoveById(collectionManager));
        commandManager.register("print_field_ascending_distance", new PrintFieldAsсendingDistance(collectionManager));

        try {
            new TCPServer("localhost", PORT, commandManager).start();
        } catch (IOException | ClassNotFoundException e) {}
    }
}



