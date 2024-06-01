package server.commands;
import global.models.Request;
import global.models.Response;

/**
 * Команда выхода
 * @author Kostya666
 */
public class Exit extends Command  {

    public Exit(){
        super(Commands.EXIT,"завершить программу");
    }

    /**
     * Выполняет команду
     * @return возвращает сообщение об успешности выполнения команды
     */
    @Override
    public Response execute(Request request){
        if (!(request.getArgs().length == 1)){
            return new Response("Неправильное количество аргументов!\nИспользование: '" + getCommandName() + "'");
        }
        System.exit(1);
        return new Response("завершение программы");
    }

}