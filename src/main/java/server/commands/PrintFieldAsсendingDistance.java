package server.commands;

import global.models.Request;
import global.models.Response;
import server.managers.CollectionManager;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Команда вывода дистанции в порядке возрастания
 * @author Kostya666
 */
public class PrintFieldAsсendingDistance extends Command{
    private final CollectionManager collectionManager;

    public PrintFieldAsсendingDistance(CollectionManager collectionManager) {
        super(Commands.PRINT_FIELD_ASCENDING_DISTANCE, "вывести значения поля distance всех элементов в порядке возрастания");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @param request запрос
     * @return успешность выполнения команды
     */
    @Override
    public Response execute(Request request) {
        if (request.getArgs().length != 1) {
            return new Response( "Неправильное количество аргументов!" +
                    "\nИспользование: '" + getCommandName() + "'");
        }

        var collection = collectionManager.getCollection();
        List<Float> res = collection.stream()
                .map(e -> e.getDistance())
                .collect(Collectors.toCollection(LinkedList::new));
        return new Response(res.toString());
    }
}
