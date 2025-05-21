package ru.korshunov.kanban.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.korshunov.kanban.httpserver.HttpKanbanServer;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHandler implements HttpHandler {

    private final TaskManager taskManager;
    private Gson gson = HttpKanbanServer.getGson();

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = getPathParts(exchange);
        int lengthPathParts = pathParts.length;

        switch (requestMethod) {
            case "GET":
                if (lengthPathParts == 2) {
                    List<Task> history = taskManager.getHistory();
                    if (history == null) {
                        responseNotFound(exchange);
                        return;
                    }
                    responseOk(exchange, gson.toJson(history));
                }
                break;
            default:
                responseNotFound(exchange);
        }
    }
}
