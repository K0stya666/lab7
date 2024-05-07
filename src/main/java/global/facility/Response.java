package global.facility;

import java.io.Serializable;

public class Response  implements Serializable {
    private static final long serialVersionUID = 5760575944040770153L;
    private String massage;
    private Object object;
    public Response (String massage){
        this.massage = massage;
    }

    private int exitCode;
    private Object responseObj;

    public Response(String s, Object obj) {
        massage = s;
        responseObj = obj;
    }

    public String getMessage(){
        return massage;
    }
}
