package server.commands;

import global.exeptions.NotFoundException;
import global.models.Request;
import global.models.Response;
import server.managers.CollectionManager;

/**
 * Команда удаления элемента из коллекции по его id
 * @author Kostya666
 */
public class RemoveById extends Command {
    private final CollectionManager collectionManager;

    public RemoveById(CollectionManager collectionManager) {
        super(Commands.REMOVE_BY_ID, "удалить элемент из коллекции по его id");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request){
        if(request.getArgs().length != 2){
            return new Response("Неправильное количество аргументов!\n" + "Использование: '" + getCommandName() + "'" );
        }
        try {
            int deletableId = Integer.parseInt(request.getArgs()[1]);
            var deletable = collectionManager.byId(deletableId);
            if (deletable == null) throw new NotFoundException();
            collectionManager.remove(deletable.getId());
            return new Response("Route удалён");
        } catch(NotFoundException e){
            return new Response("Продукта с таким id в коллекции нет!");
        }
    }
}