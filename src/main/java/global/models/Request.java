package global.models;

import server.utility.User;

import java.io.*;

public class Request implements Serializable {
    @Serial
    private static final long serialVersionUID = 5760575944040770153L;
    private final String commandName;
    private String[] args;
    private Route route;
    private User user;

    public Request(String[] args, User user) {
        this.commandName = args[0];
        this.args = args;
        this.user = user;
    }
    public Request(String[] args, Route route, User user) {
        this.commandName = args[0];
        this.args = args;
        this.route = route;
        this.user = user;
    }
//    public Request(String[] args, Route route, User user) {
//        this.commandName = args[0];
//        //this.arg = args[1];
//        this.route = route;
//        this.user = user;
//    }
//    public Request(String[] input, User user) {
//        this.commandName =  input[0];
//        this.user = user;
//    }
    public Request(String[] args) {
        this.commandName =  args[0];
        this.args = args;
        //this.user = user;
    }

    public String getCommandName() { return commandName; }
    public String[] getArgs() { return args; }
    public User getUser() { return user; }
    public Route getRoute() { return route; }

//    public String getCommand() { return commandName; }
//    //public String
//
//
//
//    public Request(String commandMassage){
//        this.arg = commandMassage;
//    }
//
//    public String getCommandMassage(){
//        return arg;
//    }
//
//    public Request(String commandMassage, Route route){
//        this.arg=commandMassage;
//        this.route = route;
//    }
//
//    public Route getRoute(){
//        return route;
//    }
//
    @Override
    public String toString(){
        return "Request [commandName=" + commandName + ", args=(" + args[0] + "," + args[1] + ")]";
    }
}
