package server.commands;

import global.models.*;
import org.slf4j.*;
import server.managers.CollectionManager;

/**
 * Команда 'remove_head'. Удаляет последний элемент коллекции.
 * @author Kostya666
 */
public class RemoveHead extends Command {
    private final CollectionManager collectionManager;
    private final Logger LOGGER = LoggerFactory.getLogger(RemoveHead.class);

    public RemoveHead(CollectionManager collectionManager) {
        super("remove_head", "вывести первый элемент коллекции и удалить его");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return успешность выполнения команды.
     */
    public Response apply(String[] args, Route route) {
        if (!args[1].isEmpty()) {
            return new Response(
                    "Неправильное количество аргументов!\nИспользование: " +
                            "'" + getName() + "'");
        }
        try {
            var collection = collectionManager.getCollection();
            int length = collection.size();
            int id = collectionManager.getCollection().get(length - 1).getId();
            collection.remove(id);
            LOGGER.info("remove " + id, true);
            collectionManager.update();
            return new Response("Маршрут успешно удалён");
        } catch (ArrayIndexOutOfBoundsException ignored) {}
        return new Response( "Коллекция пуста!");
    }
}
