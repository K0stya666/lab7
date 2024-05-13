package client.tools;

import global.models.Coordinates;
import global.models.Location;
import global.models.Route;
import global.tools.Console;

import java.util.NoSuchElementException;

public class Ask {
    public static class AskBreak extends Exception {}

    public static Route askRoute(Console console) throws AskBreak {
        Coordinates coordinates = askCoordinates(console);
        try {
            String nameFrom;
            do {
                console.print("Введите наименование пункта отправления: ");
                nameFrom = console.readln().trim();
                if (nameFrom.equals("exit")) throw new AskBreak();
            } while (nameFrom.isEmpty());
            Location from = askLocation(console, nameFrom);
            String nameTo;
            do {
                console.print("Введите наименование пункта прибытия: ");
                nameTo = console.readln().trim();
                if (nameTo.equals("exit")) throw new AskBreak();
            } while (nameTo.isEmpty());
            Location to = askLocation(console, nameTo);
            float distance;
            while (true) {
                console.print("> Дистанция между пунктами: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    distance = Float.parseFloat(line);
                    if (distance <= 1) {
                        console.printError("Дистанция должна быть больше 1. Попробуйте заново --》");
                    } else { break; }
                }
            }
            String name;
            do {
                console.print("Введите наименование маршрута: ");
                name = console.readln().trim();
                if (name.equals("exit")) throw new AskBreak();
            } while (name.isEmpty());
            return new Route(name, coordinates, from, to, distance);
        } catch (NoSuchElementException | IllegalStateException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static Coordinates askCoordinates(Console console) throws AskBreak {
        console.println("Введите координаты вашего местоположения");
        try {
            Float x;
            while (true) {
                console.print("> Координата x: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        x = Float.parseFloat(line);
                        break;
                    } catch (NumberFormatException ignored) {
                        console.printError("Неверный формат ввода. Попробуйте заново. -->");
                    }
                }
            }
            Float y;
            while (true) {
                console.print("> Координата y: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try {
                        y = Float.parseFloat(line);
                        break;
                    } catch (NumberFormatException ignored) {
                        console.printError("Неверный формат ввода. Попробуйте заново. --> ");
                    }
                }
            }
            return new Coordinates(x, y);
        } catch (IllegalStateException | NoSuchElementException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }

    public static Location askLocation(Console console, String name) throws AskBreak {
        console.println(String.format("Введите координаты местоположения %s", name));
        try {
            Long x;
            while (true) {
                console.print("> Координата x: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try { x = Long.parseLong(line); break; } catch (NumberFormatException ignored) {
                        console.printError("Неверный формат ввода. Попробуйте заново. --> ");
                    }
                }
            }
            Integer y;
            while (true) {
                console.print("> Координата y: ");
                String line = console.readln().trim();
                if (line.equals("exit")) throw new AskBreak();
                if (!line.isEmpty()) {
                    try { y = Integer.parseInt(line); break; } catch (NumberFormatException ignored) {
                        console.printError("Неверный формат ввода. Попробуйте заново. --> ");
                    }
                }
            }
            return new Location(x, y, name);
        } catch (IllegalStateException | NoSuchElementException e) {
            console.printError("Ошибка чтения");
            return null;
        }
    }
}
