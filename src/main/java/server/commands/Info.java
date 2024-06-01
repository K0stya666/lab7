package server.commands;

import global.models.Request;
import global.models.Response;
import server.managers.CollectionManager;
import java.util.Date;

/**
 * Команда выводит информации о коллекции
 * @author Kostya666
 */
public class Info extends Command {
    private final CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        super(Commands.INFO, "вывести информацию о коллекции");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request) {
        if (request.getArgs().length != 1) {
            return new Response("Неправильное количество аргументов!\nИспользование: '\" + getName() + \"'");
        }

        Date lastSaveTime = collectionManager.getLastSaveTime();
        String lastInitTimeString = (lastSaveTime == null) ? "в данной сессии инициализации еще не происходило" :
                lastSaveTime.toString();
        
        String s="" ;
        s+="Сведения о коллекции:\n";
        s+=" Тип: " + collectionManager.getCollection().getClass().toString();
        s+=" \nКоличество элементов: " + collectionManager.getCollection().size();
        s+=" \nДата последней инициализации: " + lastInitTimeString;
        s+="\n";
        return new Response(s);
    }
}