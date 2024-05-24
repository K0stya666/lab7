package server.commands;
import global.models.Response;
import global.models.Route;
import server.managers.CollectionManager;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда,которая выводит дистанции в порядке возрастания
 * @author Kostya666
 */
public class PrintFieldAsсendingDistance extends Command{

    private final CollectionManager collectionManager;

    public PrintFieldAsсendingDistance(CollectionManager collectionManager) {
        super("print_field_ascending_distance", "вывести значения поля distance всех элементов в порядке возрастания");
        this.collectionManager = collectionManager;
    }

    public Response apply(String[] args, Route route) {
        if (!args[1].isEmpty()) {
            return new Response( "Неправильное количество аргументов!" +
                    "\nИспользование: '" + getName() + "'");
        }

        var collection = collectionManager.getCollection();

        List<Float> res = collection.stream()
                .map(e -> e.getDistance())
                .collect(Collectors.toCollection(LinkedList::new));
        return new Response(res.toString());
    }
}
