package server.commands;

/**
 * Команды
 * @author Kostya666
 */
public enum Commands {
    ADD,
    CLEAR,
    EXIT,
    EXECUTE_SCRIPT,
    HELP,
    HISTORY,
    INFO,
    LOGIN,
    PRINT_ASCENDING,
    PRINT_FIELD_ASCENDING_DISTANCE,
    REMOVE_BY_ID,
    REMOVE_HEAD,
    SHOW,
    SIGNUP,
    UPDATE_BY_ID,
    NONAME_COMMAND;

    public boolean equals(String s) {
        return this.name().equalsIgnoreCase(s);
    }
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static Commands getCommandName(String commandName) {
        return switch (commandName) {
            case "add" -> ADD;
            case "clear" -> CLEAR;
            case "exit" -> EXIT;
            case "help" -> HELP;
            case "history" -> HISTORY;
            case "info" -> INFO;
            case "login" -> LOGIN;
            case "print_ascending" -> PRINT_ASCENDING;
            case "print_field_ascending_distance" -> PRINT_FIELD_ASCENDING_DISTANCE;
            case "remove_by_id" -> REMOVE_BY_ID;
            case "remove_head" -> REMOVE_HEAD;
            case "show" -> SHOW;
            case "signup" -> SIGNUP;
            case "update_by_id" -> UPDATE_BY_ID;
            case "execute_script" -> EXECUTE_SCRIPT;
            default -> NONAME_COMMAND;
        };
    }

//    public static String getCommandName(Command commandName) {
//        String s = switch (commandName) {
//            case ADD -> "add";
//            case CLEAR -> "clear";
//            case EXIT -> "exit";
//            case HELP -> "help";
//            case HISTORY -> "history";
//            case INFO -> "info";
//            case LOGIN -> "login";
//            case PRINT_ASCENDING -> "print_ascending";
//            case PRINT_FIELD_ASCENDING_DISTANCE -> "print_field_ascending_distance";
//            case REMOVE_BY_ID -> "remove_by_id";
//            case REMOVE_HEAD -> "remove_head";
//            case SHOW -> "show";
//            case SIGNUP -> "signup";
//            case UPDATE_BY_ID -> "update_by_id";
//            case EXECUTE_SCRIPT -> "execute_script";
//            default -> "NONAME_COMMAND";
//        };
//        return s;
//    }
}
