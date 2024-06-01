package server.commands;

import global.models.*;
import server.managers.CollectionManager;

/**
 * Команда вывода в стандартный поток всех элементов коллекции в строковом представлении
 * @author Kostya666
 */
public class Show extends Command {
    private final CollectionManager collectionManager;

    public Show(CollectionManager collectionManager) {
        super(Commands.SHOW, "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request) {
        if (request.getArgs().length != 1) {
            return new Response("Неправильное количество " +
                    "аргументов!\nИспользование: '\" + getName() + \"'");
        }
        return new Response(collectionManager.toString());
    }
}