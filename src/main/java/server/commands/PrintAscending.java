package server.commands;

import global.models.*;
import server.managers.CollectionManager;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Команда вывода элементов коллекции в порядке возрастания
 * @author Kostya666
 */
public class PrintAscending extends Command{
    private final CollectionManager collectionManager;

    public PrintAscending(CollectionManager collectionManager) {
        super(Commands.PRINT_ASCENDING,"вывести элементы коллекции в порядке возрастания");
        this.collectionManager = collectionManager;
    }

    public Response execute(Request request) {
        if (request.getArgs().length != 1) {
            return new Response("Неправильное количество аргументов!" +
                    "\nИспользование: '" + getCommandName() + "'");
        }

        LinkedList<Route> newCollection = collectionManager
                .getCollection()
                .stream()
                .collect(Collectors.toCollection(LinkedList::new));

        return new Response(newCollection.toString());

    }
}
