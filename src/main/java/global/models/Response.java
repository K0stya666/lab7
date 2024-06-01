package global.models;

import server.managers.databases.UserManager;

import java.io.Serial;
import java.io.Serializable;

public class Response  implements Serializable {
    @Serial
    private static final long serialVersionUID = 5760575944040770153L;
    private final String massage;

    public Response (String massage, boolean authorized){
        this.massage = massage;
        UserManager.authorized = authorized;
    }
    public Response (String massage){
        this.massage = massage;
    }

    private Object responseObj;

    public Response(String s, Object obj) {
        massage = s;
        responseObj = obj;
    }

    public String getMessage(){
        return massage;
    }

    @Override
    public String toString() {
        return massage;
    }
}
