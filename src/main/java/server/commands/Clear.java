package server.commands;

import global.models.Response;
import global.models.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.managers.CollectionManager;

import java.util.List;
import java.util.Stack;

/**
 * Команда очищает коллекцию.
 */
public class Clear extends Command {

    private final CollectionManager collectionManager;
    private final Logger LOGGER = LoggerFactory.getLogger(Clear.class);

    public Clear(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;

    }
    /**
     * Метод выполняет команду
     *
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response apply (String[] arguments , Route route){
        if(!arguments[1].isEmpty()){
            return new Response("Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }
        List<Route> collection = collectionManager.getCollection();
        collection.clear();
        collectionManager.update();
        return new Response("Коллекция очищена");
    }
}
