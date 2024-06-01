package server.commands;

import global.models.*;
import server.managers.CollectionManager;

/**
 * Команда добавления элемента в коллекцию
 * @author Kostya666
 */
public class Add extends Command {
    private final CollectionManager collectionManager;

    public Add(CollectionManager collectionManager){
        super(Commands.ADD, "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request) {
        if(request.getArgs().length != 1){
            return new Response("Неправильное количество аргументов!\n" + "Использование: '" + getCommandName() + "'" );
        }

        var route = request.getRoute();
        var user = request.getUser();

       if(route != null && route.validate()){
           collectionManager.add(route, user);
           return new Response("Route добавлен!");
       } else {
           return new Response("Поля Route не валидны! Route не создан!");
       }
    }
}