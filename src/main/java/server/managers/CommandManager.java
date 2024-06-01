package server.managers;

import server.commands.Command;
import server.commands.Commands;

import java.util.*;

public class CommandManager {
    protected final Map<String, Command> commands = new LinkedHashMap<>();
    private final List<String> commandHistory = new ArrayList<>();

    /**
     * Добавляет команду
     * @param commandName Название команды.
     * @param command Команда.
     */
    public void register(Commands commandName, Command command) {
        commands.put(commandName.toString(), command);
    }

    /**
     * @return Словарь команд.
     */
    public Map<String, Command> getCommands() {
        return commands;
    }

    public Command getCommand(String commandName) { return commands.get(commandName); }

    /**
     * @return История команд.
     */
    public List<String> getCommandHistory() {
        return commandHistory;
    }

    /**
     * Добавляет команду в историю.
     * @param command Команда.
     */
    public void addToHistory(String command) {
        commandHistory.add(command);
    }
}
