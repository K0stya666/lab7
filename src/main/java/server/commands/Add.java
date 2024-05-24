package server.commands;

import global.models.Response;
import global.models.Route;
import server.managers.CollectionManager;


/**
 * Добавление элемента в коллекцию
 * @author Kostya666
 */

public class Add extends Command{
    private final CollectionManager collectionManager;

    public Add( CollectionManager collectionManager){
        super("add", "добавить новый элемент в коллекцию");
        this.collectionManager=collectionManager;
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */

    public Response apply(String[] arguments , Route route){
        if(!arguments[1].isEmpty()){
            return new Response("Неправильное количество аргументов!\n" + "Использование: '" + getName() + "'" );
        }

       if(route != null && route.validate()){
            collectionManager.add(route, 666);  // добавить userId
            return new Response("Route добавлен!");
        }else{
            return new Response("Поля Route не валидны! Route не создан!");
        }
    }
}