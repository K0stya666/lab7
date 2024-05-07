package server.commands;

import global.facility.Response;
import global.facility.Route;
import server.rulers.CollectionManager;

/**
 * команда сохраняющая коллекцию в файл
 */
public class Save  extends Command{

    private final CollectionManager collectionRuler;

    public Save(CollectionManager collectionRuler){
        super("save", "сохранить коллекцию");

        this.collectionRuler = collectionRuler;
    }
    /**
     * метод выполняет команду
     *
     * @return возвращает сообщение о  успешности выполнения команды
     */
    @Override
    public Response apply(String[] arguments , Route route){
        if(!arguments[1].isEmpty()){
            return new Response("Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }

        collectionRuler.saveCollection();
        return new Response("Выполнение сохранения прошло успешно");
    }
}