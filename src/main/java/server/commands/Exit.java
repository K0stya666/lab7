package server.commands;
import global.models.Response;
import global.models.Route;

/**
 * Команда выхода
 */
public class Exit extends Command  {


    public Exit(){
        super("exit","завершить программу");
    }
    /**
     * Метод выполняет команду
     *
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response apply(String[] arguments , Route ticket){
        if(!arguments[1].isEmpty()){
            return new Response("Неправильное количество аргументов!\nИспользование: '" + getName() + "'");
        }

        System.exit(1);
        return new Response("завершение программы");
    }

}