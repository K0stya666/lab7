package global.models;

import global.tools.Idgenerator;
import global.tools.Validatable;
import server.managers.CollectionManager;

import java.io.Serial;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Route implements Validatable, Serializable, Comparable<Route> {
    private static final Idgenerator idgenerator = new Idgenerator();
    @Serial
    private static final long serialVersionUID = 5760575944040770153L;
    private static Integer nextId = 1;

    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private final String name; //Поле не может быть null, Строка не может быть пустой
    private final Coordinates coordinates; //Поле не может быть null
    private final Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private final Location from; //Поле не может быть null
    private final Location to; //Поле может быть null
    private final float distance;//Значение поля должно быть больше 1

    public Route(String name, Coordinates coordinates, Date creationDate, Location from, Location to, float distance) {
        this.id = Math.toIntExact(idgenerator.generateID());
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public Route(String name, Coordinates coordinates, Location from, Location to, float distance) {
        this(name, coordinates, new Date(), from, to, distance);
        this.id = Math.toIntExact(idgenerator.generateID());
    }

    public Route (int id, String name, Coordinates coordinates, Date creationDate, Location from, Location to, float distance) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public static void updateNextId(CollectionManager collectionManager) {
        var maxId = collectionManager
                .getCollection()
                .stream().filter(Objects::nonNull)
                .map(Route::getId)
                .mapToInt(Integer::intValue).max().orElse(0);
        nextId = maxId + 1;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public float getDistance() {
        return distance;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Integer getId() {
        return id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getName() {
        return name;
    }

    public static Route fromArray(String[] r) {
        Integer id;
        String name;
        Date creationDate;
        Coordinates coordinates;
        Location from;
        Location to;
        float distance;
        try {
            try {
                id = Integer.parseInt(r[0]);
            } catch (NumberFormatException e) {
                id = null;
            }
            name = r[1];
            coordinates = new Coordinates(r[2]);
            try {
                String pattern = "EEE MMM dd HH:mm:ss zzz yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
                creationDate = dateFormat.parse(r[3]);
            } catch (ParseException e) {
                creationDate = null;
            }
            from = new Location(r[4]);
            to = new Location(r[5]);
            distance = Float.parseFloat(r[6]);
            return new Route(name, coordinates, creationDate, from, to, distance);
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return null;
    }

    public static String[] toArray(Route route) {
        var list = new ArrayList<String>();
        list.add(route.getId().toString());
        list.add(route.getName());
        list.add(route.getCoordinates().toString());
        list.add(route.getCreationDate().toString());
        list.add(route.getFrom().toString());
        list.add(route.getTo().toString());
        list.add(String.valueOf(route.getDistance()));
        return list.toArray(new String[0]);
    }

    @Override
    public int compareTo(Route route) {
        return (int) (this.distance - route.getDistance());
    }


    @Override
    public boolean validate() {
        if (id == null | id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null) return false;
        if (creationDate == null) return false;
        if (to == null) return false;
        if (distance <= 1) return false;
        return true;
    }


    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", from=" + from +
                ", to=" + to +
                ", distance=" + distance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(id, route.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, from, to, distance);
    }

    public void setId(Integer id) {
        this.id = id;
    }


}

