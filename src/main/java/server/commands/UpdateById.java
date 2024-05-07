package server.commands;

import global.exeptions.NotFoundException;
import global.facility.Response;
import global.facility.Route;
import server.rulers.CollectionManager;

/**
 * команда обновляющая значение элемента коллекции, id которого равен заданному
 */
public class UpdateById extends Command{

    private final CollectionManager collectionManager;

    public UpdateById(CollectionManager collectionManager){
        super("update_by_id" , "обновить значение элемента коллекции, id которого равен заданному");

        this.collectionManager=collectionManager;
    }
    /**
     * метод выполняет команду
     *
     * @return возвращает сообщение о  успешности выполнения команды
     */
    @Override
    public Response apply(String[] arguments , Route ticket) {
        if(arguments[1].isEmpty()){
            return new Response("Неправильное количество аргументов!\n" + "Использование: '" + getName() + "'" );
        }
        try{
            long deletableId= Long.parseLong(arguments[1]);
            var deletable= collectionManager.byId((int) deletableId);
            if (deletable == null) throw new NotFoundException();
            collectionManager.remove(deletable.getId());
            Route a =  ticket;
            if(a!= null&&a.validate()){
                collectionManager.add(a);
                //console.println("Ticket добавлен!");
                return new Response("Route добавлен!");
            }else{
                //console.printError("Поля Ticket не валидны! Ticket не создан!");
                return new Response("Поля Route не валидны! Route не создан!");
            }
        }catch(NotFoundException e){
            //console.printError("Продукта с таким ID в коллекции нет!");
            return new Response("Продукта с таким ID в коллекции нет!");
        }
    }
}