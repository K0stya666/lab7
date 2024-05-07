package global.facility;


import global.tools.Validatable;

import java.io.Serializable;

public class Coordinates implements Validatable, Serializable {

    private Float x; // Поле не может быть null
    private Float y; // Поле не может быть null


    public Coordinates(Float x, Float y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(String s) {
        try {
            try { this.x = Float.parseFloat(s.split(";")[0]); } catch (NumberFormatException ignored) {}
            try { this.y = Float.parseFloat(s.split(";")[1]); } catch (NumberFormatException ignored) {}
        } catch (ArrayIndexOutOfBoundsException ignored) {}
    }

    @Override
    public boolean validate() {
        return x != null && y != null;
    }

    @Override
    public String toString() {
        return x + "; " + y;
    }
}
