package server.commands;

import global.models.Response;
import global.models.Route;
import server.managers.CollectionManager;

import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Команда, которая выводит элементы коллекции в порядке возрастания.
 * @author Kostya666
 */
public class PrintAscending extends Command{
    private final CollectionManager collectionManager;

    public PrintAscending(CollectionManager collectionManager) {
        super("print_ascending","вывести элементы коллекции в порядке возрастания");
        this.collectionManager = collectionManager;
    }

    public Response apply(String[] args, Route route) {
        if (!args[1].isEmpty()) {
            return new Response("Неправильное количество аргументов!" +
                    "\nИспользование: '" + getName() + "'");
        }
//        var collection = collectionManager.getCollection();
//        LinkedList<Route> newCollection = new LinkedList<>();
//        for (var e : collection) {
//            newCollection.add(e);
//        }
//        return new Response(newCollection.toString());

        LinkedList<Route> newCollection = collectionManager
                .getCollection()
                .stream()
                .collect(Collectors.toCollection(LinkedList::new));

        return new Response(newCollection.toString());

    }
}
