package server.commands;

import global.models.*;
import server.managers.CollectionManager;
import server.managers.databases.Interstate60;

import java.util.List;


/**
 * Команда очистки коллекции
 * @author Kostya666
 */
public class Clear extends Command {
    private final CollectionManager collectionManager;
    private static final Interstate60 interstate60 = new Interstate60();

    public Clear(CollectionManager collectionManager) {
        super(Commands.CLEAR, "очистить коллекцию");
        this.collectionManager = collectionManager;

    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request){
        if(request.getArgs().length != 1){
            return new Response("Неправильное количество аргументов!\nИспользование: '" + getCommandName() + "'");
        }

        List<Route> collection = collectionManager.getCollection();
//        collection.clear();
//        interstate60.clearRoutes();
        collectionManager.clear();
        collectionManager.update();
        return new Response("Коллекция очищена");
    }
}
