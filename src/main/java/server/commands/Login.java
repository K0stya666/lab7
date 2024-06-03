package server.commands;

import global.models.*;
import server.managers.databases.UserManager;

/**
 * Команда входа пользователя в свою учётную запись
 * @author Kostya666
 */
public class Login extends Command {
    private final UserManager userManager;

    public Login(UserManager userManager) {
        super(Commands.LOGIN, "авторизоваться");
        this.userManager = userManager;
    }

    /**
     * Выполняет команду
     * @param request запрос
     * @return успешность выполнения команды
     */
    @Override
    public Response execute(Request request) {
        if (request.getArgs().length != 3) return new Response("Неправильное количество аргументов.\nИспользование: '" + getCommandName() + "'");
//        var username = request.getArgs()[0];
//        var password = request.getArgs()[1];
        var user = request.getUser();

        if (userManager.checkUser(user)) {
            return new Response("Вы вошли под именем: " + user.getUsername(), true);
        } else {
            return new Response("Ошибка во время авторизации пользователя");
        }
    }
}
