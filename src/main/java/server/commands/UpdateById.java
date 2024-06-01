package server.commands;

import global.exeptions.NotFoundException;
import global.models.*;
import server.managers.CollectionManager;

/**
 * Команда обновления значения элемента коллекции, id которого равен заданному
 * @author Kostya666
 */
public class UpdateById extends Command{
    private final CollectionManager collectionManager;

    public UpdateById(CollectionManager collectionManager){
        super(Commands.UPDATE_BY_ID , "обновить значение элемента коллекции, id которого равен заданному");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request) {
        if(request.getArgs().length != 2){
            return new Response("Неправильное количество аргументов!\n" + "Использование: '" + getCommandName() + "'" );
        }

        try{
            long deletableId= Long.parseLong(request.getArgs()[1]); // LOOOOOONG
            var deletable= collectionManager.byId((int) deletableId);
            if (deletable == null) throw new NotFoundException();
            collectionManager.remove(deletable.getId());
            Route a = request.getRoute();
            if(a != null && a.validate()){
                return new Response("Route добавлен!");
            }else{
                return new Response("Поля Route не валидны! Route не создан!");
            }
        }catch(NotFoundException e){
            return new Response("Продукта с таким ID в коллекции нет!");
        }
    }
}