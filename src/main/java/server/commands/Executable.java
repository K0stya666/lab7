package server.commands;

import global.facility.Response;
import global.facility.Route;

/**
 * Интерфейс для всех комманд
 */
public interface Executable {
    Response apply(String[] arguments , Route route);
}
