package server.commands;

import global.facility.Response;
import global.facility.Route;
import server.rulers.CommandManager;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * команда выводящая последние 13 использованных команд
 */
public class History extends Command{
    private final CommandManager commandManager;

    public History( CommandManager commandRuler) {
        super("history", "вывести последние 13 комманд");
        this.commandManager = commandRuler;
    }
    /**
     * метод выполняет команду
     *
     * @return возвращает сообщение о  успешности выполнения команды
     */
    @Override
    public Response apply(String[] arguments , Route ticket){
        if (!arguments[1].isEmpty()) {

            return new Response("Неправильное количество аргументов!\nИспользование: '\" + getName() + \"'");
        }

        List<String> myHistory = new ArrayList<>(commandManager.getCommandHistory()); // Создаем копию CommandHistory
        int startIndex = Math.max(0, myHistory.size() - 13); // Начальный индекс для вывода последних 13 команд
        List<String> last13Commands = myHistory.subList(startIndex, myHistory.size()); // Получаем последние 13 команд


        String stringHistory = last13Commands.stream()
                .collect(Collectors.joining("\n"));
        return new Response(stringHistory);
    }

}