package global.models;

import java.io.Serializable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class Request implements Serializable {
    private static final long serialVersionUID = 5760575944040770153L;
    private String commandMassage;
    private Route route;
    public Request(String commandMassage){
        this.commandMassage = commandMassage;
    }

    public String getCommandMassage(){
        return commandMassage;
    }

    public Request(String commandMassage, Route route){
        this.commandMassage=commandMassage;
        this.route = route;
    }

    public Route getRoute(){
        return route;
    }

    @Override
    public String toString(){
        return commandMassage;
    }
}
