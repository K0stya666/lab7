package server.commands;

import global.models.*;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Абстрактная команда, с помощью которой мы задаем имя и описание команды
 * @author Kostya666
 */
public abstract class Command {
    private final Commands commandName;
    private final String description;

    public Command(Commands commandName, String description){
        this.commandName =commandName;
        this.description=description;
    }

    public abstract Response execute(Request request) throws IOException, ClassNotFoundException, InterruptedException;

    public Commands getCommandName() {
        return commandName;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + commandName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object obj){
        if(this==obj) return true;
        if(obj ==null|| this.getClass()!=obj.getClass()) return false;
        Command command = (Command) obj;
        return commandName.equals(command.commandName)&&description.equals(command.description);
    }
    @Override
    public int hashCode(){
        return commandName.hashCode()+description.hashCode();
    }
}
