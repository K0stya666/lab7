package server;

import server.commands.*;
import server.managers.databases.Interstate60;
import server.managers.TCPServer;
import server.managers.CollectionManager;
import server.managers.CommandManager;
import server.managers.databases.UserManager;
import static server.commands.Commands.*;
import java.io.IOException;

import static server.managers.databases.Interstate60.createDatabaseIfNotExists;


public class Main {
    private final static int PORT = 4129;
    private final static String HOST = "localhost";

    public static void main(String[] args) {
        createDatabaseIfNotExists();

        var commandManager = new CommandManager();
        var interstate60 = new Interstate60();
        var collectionManager = new CollectionManager(interstate60);
        var userManager = new UserManager();

        if (!collectionManager.loadCollection()) {
            System.err.println("Error: Collection could not be loaded!");
            return;
        }

        commandManager.register(SIGNUP, new Signup(userManager));
        commandManager.register(LOGIN, new Login(userManager));

        commandManager.register(ADD, new Add(collectionManager));
        commandManager.register(CLEAR, new Clear(collectionManager));
        commandManager.register(SHOW, new Show(collectionManager));
        commandManager.register(HELP, new Help(commandManager));
        commandManager.register(UPDATE_BY_ID, new UpdateById(collectionManager));
        commandManager.register(HISTORY, new History(commandManager));
        commandManager.register(EXIT, new Exit());
        commandManager.register(INFO, new Info(collectionManager));
        commandManager.register(REMOVE_HEAD, new RemoveHead(collectionManager));
        commandManager.register(PRINT_ASCENDING, new PrintAscending(collectionManager));
        commandManager.register(REMOVE_BY_ID, new RemoveById(collectionManager));
        commandManager.register(PRINT_FIELD_ASCENDING_DISTANCE, new PrintFieldAsсendingDistance(collectionManager));

        try {
            new TCPServer(HOST, PORT, commandManager).start();
        } catch (IOException | ClassNotFoundException ignored) {}
    }
}



