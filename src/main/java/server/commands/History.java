package server.commands;

import global.models.Request;
import global.models.Response;
import server.managers.CommandManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда вывода последних 13 использованных команд
 */
public class History extends Command {
    private final CommandManager commandManager;

    public History( CommandManager commandRuler) {
        super(Commands.HISTORY, "вывести последние 13 команд");
        this.commandManager = commandRuler;
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request){
        if (request.getArgs().length != 1) {

            return new Response("Неправильное количество аргументов!\nИспользование: '\" + getName() + \"'");
        }

        List<String> myHistory = new ArrayList<>(commandManager.getCommandHistory()); // Создаем копию CommandHistory
        int startIndex = Math.max(0, myHistory.size() - 13); // Начальный индекс для вывода последних 13 команд
        List<String> last13Commands = myHistory.subList(startIndex, myHistory.size()); // Получаем последние 13 команд
        String stringHistory = String.join("\n", last13Commands);
        return new Response(stringHistory);
    }

}