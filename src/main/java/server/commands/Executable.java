package server.commands;

import global.models.Response;
import global.models.Route;

/**
 * Интерфейс для всех комманд
 */
public interface Executable {
    Response apply(String[] arguments , Route route);
}
