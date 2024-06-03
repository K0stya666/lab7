package server.commands;

import global.models.Request;
import global.models.Response;
import server.managers.CommandManager;

/**
 * Команда вывода всех доступных команд
 */
public class Help extends Command {
    private final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        super(Commands.HELP, "вывести справку по доступным командам");
        this.commandManager = commandManager;

    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request) {
        if (request.getArgs().length != 1) {
            return new Response("Неправильное количество аргументов!\nИспользование: '" + request.getCommandName() + "'");
        }

        StringBuilder result = new StringBuilder();
        commandManager.getCommands().values().forEach(command -> {
            result.append(command.getCommandName()).append(" : ").append(command.getDescription()).append("\n");
        });
        return new Response(result.toString());
    }
}