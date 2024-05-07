package server.commands;

import global.facility.Response;
import global.facility.Route;
import server.rulers.CollectionManager;


/**
 * добавление элемента в коллекцию
 */

public class Add extends Command{
    private final CollectionManager collectionRuler;

    public Add( CollectionManager collectionRuler){
        super("add", "добавить новый элемент в коллекцию");
        this.collectionRuler=collectionRuler;
    }

    /**
     * метод выполняет команду
     *
     * @return возвращает сообщение о  успешности выполнения команды
     */

    public Response apply(String[] arguments , Route ticket){
        try{
            if(!arguments[1].isEmpty()){
                return new Response("Неправильное количество аргументов!\n" + "Использование: '" + getName() + "'" );
            }
            //console.println("Начинаем создание Route");
            Route a =  ticket;
            if(a!= null&&a.validate()){
                collectionRuler.add(a);
                //console.println("Route добавлен!");
                return new Response("Route добавлен!");
            }else{
                //console.printError("Поля Route не валидны! Route не создан!");
                return new Response("Поля Route не валидны! Route не создан!");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}