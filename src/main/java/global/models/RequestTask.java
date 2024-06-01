package global.models;

import server.managers.CommandManager;

import java.util.concurrent.RecursiveTask;

public class RequestTask extends RecursiveTask<Response> {
    private final Request request;
    private final CommandManager commandManager;

    public RequestTask(Request request, CommandManager commandManager) {
        this.request = request;
        this.commandManager = commandManager;
    }

    @Override
    protected Response compute() {

        return new Response(request.toString());
    }
}
