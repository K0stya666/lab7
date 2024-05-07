package server.commands;

import global.facility.Response;
import global.facility.Route;
import server.rulers.CollectionManager;

import java.util.Stack;

/**
 * Команда очищает коллекцию.
 */
public class Clear extends Command {

    private final CollectionManager collectionManager;

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



        var isFirst = true;
        Stack<Route> stack = collectionManager.getCollection();
        while (!stack.isEmpty()) {
            var route1 = stack.pop();
            collectionManager.remove(route1.getId());
            collectionManager.addLog("remove " + route1.getId(), isFirst);
            isFirst = false;
        }

        collectionManager.update();
        return new Response("Коллекция очищена");
    }
}
