package server.commands;

import global.exeptions.NotFoundException;
import global.models.Response;
import global.models.Route;
import server.managers.CollectionManager;

/**
 * команда удаляющая элемент из коллекции по его id
 */
public class RemoveById extends Command {
    private final CollectionManager collectionRuler;


    public RemoveById(CollectionManager collectionRuler) {
        super("remove_by_id", "удалить элемент из коллекции по его id");

        this.collectionRuler = collectionRuler;
    }
    /**
     * метод выполняет команду
     *
     * @return возвращает сообщение о  успешности выполнения команды
     */
    @Override
    public Response apply(String[] arguments , Route ticket){
        if(arguments[1].isEmpty()){
            //console.println("Неправильное количество аргументов!");
            //console.println("Использование: '" + getName() + "'");
            return new Response("Неправильное количество аргументов!\n" + "Использование: '" + getName() + "'" );
        }
        try{
            long deletableId= Long.parseLong(arguments[1]);
            var deletable= collectionRuler.byId((int) deletableId);
            if (deletable == null) throw new NotFoundException();
            collectionRuler.remove(deletable.getId());
            return new Response("Route удалён");
        }catch(NotFoundException e){
            //console.printError("Продукта с таким ID в коллекции нет!");
            return new Response("Продукта с таким ID в коллекции нет!");
        }
    }
}