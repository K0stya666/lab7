package server.commands;

import global.models.Request;
import global.models.Response;
import server.managers.databases.UserManager;
import server.utility.User;

import java.security.NoSuchAlgorithmException;

/**
 * Команда регистрации нового пользователя
 * @author Kostya666
 */
public class Signup extends Command {
    private final UserManager userManager;

    public Signup(UserManager userManager) {
        super(Commands.SIGNUP, "создать нового пользователя");
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
        try {
            var username = request.getArgs()[1];
            var password = request.getArgs()[2];
            var user = new User(username, password);

            if (userManager.addUser(user)) {
                return new Response("Пользователь " + user.getUsername() + " успешно зарегистрирован", true);
            } else {
                return new Response("По какой-то причине пользователя " + user.getUsername() + " не получилось зарегистрировать");
            }
        } catch (ArrayIndexOutOfBoundsException | NoSuchAlgorithmException e) { return new Response("login or password not valid"); }
    }
}
