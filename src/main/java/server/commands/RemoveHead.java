package server.commands;

import global.models.*;
import org.slf4j.*;
import server.managers.CollectionManager;

/**
 * Команда удаления последнего элемента коллекции
 * @author Kostya666
 */
public class RemoveHead extends Command {
    private final CollectionManager collectionManager;
    private final Logger LOGGER = LoggerFactory.getLogger(RemoveHead.class);

    public RemoveHead(CollectionManager collectionManager) {
        super(Commands.REMOVE_HEAD, "вывести первый элемент коллекции и удалить его");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return успешность выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        if (request.getArgs().length != 1) {
            return new Response(
                    "Неправильное количество аргументов!\nИспользование: " +
                            "'" + getCommandName() + "'");
        }
        try {
            var collection = collectionManager.getCollection();
            int length = collection.size();
            int id = collectionManager.getCollection().get(0).getId();
            //collection.remove(id);
            collectionManager.remove(id);
            LOGGER.info("remove {}", id);
            collectionManager.update();
            return new Response("Маршрут успешно удалён");
        } catch (ArrayIndexOutOfBoundsException ignored) {}
        return new Response( "Коллекция пуста!");
    }
}
