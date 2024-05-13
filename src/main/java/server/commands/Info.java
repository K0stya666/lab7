package server.commands;


import global.models.Response;
import global.models.Route;
import server.managers.CollectionManager;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Команда, которая выводит информацию о коллекции
 * @author Kostya666
 */
public class Info extends Command {
    private final CollectionManager collectionManager;

    public Info(CollectionManager collectionManager) {
        super("info", "вывести информацию о коллекции");

        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response apply(String[] arguments , Route ticket) {
        if (!arguments[1].isEmpty()) {
            return new Response("Неправильное количество аргументов!\nИспользование: '\" + getName() + \"'");
        }

        Date lastInitTime = collectionManager.getLastInitTime();
        String lastInitTimeString = (lastInitTime == null) ? "в данной сессии инициализации еще не происходило" :
                lastInitTime.toString();
        
        String s="" ;
        s+="Сведения о коллекции:\n";
        s+=" Тип: " + collectionManager.getCollection().getClass().toString();
        s+=" \nКоличество элементов: " + collectionManager.getCollection().size();
        s+=" \nДата последней инициализации: " + lastInitTimeString;
        s+="\n";
        return new Response(s);
    }
}