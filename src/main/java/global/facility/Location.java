package global.facility;


import global.tools.Validatable;

import java.io.Serializable;

public class Location implements Validatable, Serializable {


    private Long x; // Поле не может быть null
    private Integer y; // Поле не может быть null
    private String name; // Длина строки не должна быть больше 502, Поле не может быть null

    public Location(Long x, Integer y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }
    public Location(String s) {
        try {
            try { this.x = Long.parseLong(s.split(";")[0]); } catch (NumberFormatException ignored) {}
            try { this.y = Integer.parseInt(s.split(";")[1]); } catch (NumberFormatException ignored) {}
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    @Override
    public boolean validate() {
        if (x == null || y == null) return false;
        return name.length() <= 502;
    }


    @Override
    public String toString() {
        return "(" + x + "," + y + ", \"" + name + "\")";
    }


}
