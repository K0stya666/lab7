package server.commands;

import global.facility.Response;
import global.facility.Route;
import server.rulers.CommandManager;

/**
 * команда выводящая все доступные команды
 */
public class Help extends Command {
    private final CommandManager commandManager;


    public Help(CommandManager commandManager) {
        super("help", "вывести справку по доступным командам");
        this.commandManager = commandManager;

    }
    /**
     * метод выполняет команду
     *
     * @return возвращает сообщение об  успешности выполнения команды
     */
    @Override
    public Response apply(String[] arguments , Route route) {
        if (!arguments[1].isEmpty()) {
            return new Response("Неправильное количество аргументов!\nИспользование: '\" + getName() + \"'");
        }

        StringBuilder result = new StringBuilder();
        commandManager.getCommands().values().forEach(command -> {
            result.append(command.getName() + " : " + command.getDescription()+"\n\n");
        });
        return new Response(result.toString());
    }
}