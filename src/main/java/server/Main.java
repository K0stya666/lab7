package server;

import global.tools.Console;
import global.tools.StandartConsole;
import server.commands.*;
import server.managers.SocketServer;
import server.rulers.CollectionManager;
import server.rulers.CommandManager;
import server.tools.CSVparser;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        if (args.length < 2) {
            System.err.println("Usage: java -jar server.jar <dataFileName> <port>");
            return;
        }

        String dataFileName = args[0]; // Имя файла из аргумента
        int port; // Порт из аргумента
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Error: Port must be an integer.");
            return;
        }

        Console console = new StandartConsole();
        CSVparser csVparser = new CSVparser(dataFileName, console);
        CollectionManager collectionManager = new CollectionManager(csVparser);
        CommandManager commandManager = new CommandManager();
        if (!collectionManager.loadCollection()) {
            System.err.println("Error: Collection could not be loaded!");
            return;
        }

        commandManager.register("add", new Add(collectionManager));
        commandManager.register("clear", new Clear(collectionManager));
        commandManager.register("save", new Save(collectionManager));
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

        new SocketServer("localhost", port, commandManager).start();
    }
}



