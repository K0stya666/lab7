package server.commands;

import global.facility.Response;
import global.facility.Route;
import server.rulers.CollectionManager;

/**
 * Команда 'remove_head'. Удаляет последний элемент коллекции.
 * @author Kostya666
 */
public class RemoveHead extends Command {
    private final CollectionManager collectionManager;

    public RemoveHead(CollectionManager collectionManager) {
        super("remove_head", "вывести первый элемент коллекции и удалить его");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return Успешность выполнения команды.
     */
    public Response apply(String[] args, Route route) {
        if (!args[1].isEmpty()) {
            return new Response(
                    "Неправильное количество аргументов!\nИспользование: " +
                            "'" + getName() + "'");
        }
        try {
            Integer id = collectionManager.getCollection().peek().getId();
            collectionManager.getCollection().pop();
            collectionManager.addLog("remove " + id, true);
            collectionManager.update();
            return new Response("Маршрут успешно удалён");
        } catch (ArrayIndexOutOfBoundsException ignored) {}
        return new Response( "Колекция пуста!");
    }
}
