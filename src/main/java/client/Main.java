package client;

import client.commands.ExecuteScript;
import client.managers.Client;
import client.tools.Ask;
import org.checkerframework.checker.units.qual.C;
import server.commands.Exit;
import server.managers.CommandManager;
import static server.commands.Commands.*;

import java.io.IOException;

public class Main {
    private static final int PORT = 4129;
    private static final String HOST = "localhost";
    private static final CommandManager commandManager = new CommandManager();

    public static void main(String[] args) throws Ask.AskBreak, IOException, ClassNotFoundException, InterruptedException {

        commandManager.register(EXIT, new Exit());
        commandManager.register(EXECUTE_SCRIPT, new ExecuteScript(commandManager));

        Client client = new Client(HOST, PORT, commandManager);
        client.start();
    }
}
