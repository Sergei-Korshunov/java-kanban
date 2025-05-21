package ru.korshunov.kanban.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.korshunov.kanban.httpserver.HttpKanbanServer;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.task.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class SubtasksHandler extends BaseHandler implements HttpHandler {

    private final TaskManager taskManager;
    private Gson gson = HttpKanbanServer.getGson();

    public SubtasksHandler(TaskManager taskManager) {
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
                    List<Subtask> subtasks = taskManager.getListOfSubtasks();
                    if (subtasks == null) {
                        responseNotFound(exchange);
                        return;
                    }
                    responseOk(exchange, gson.toJson(subtasks));

                } else if (lengthPathParts == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    Subtask subtask = taskManager.getSubtaskOnId(id);
                    if (subtask == null) {
                        responseNotFound(exchange);
                        return;
                    }
                    responseOk(exchange, gson.toJson(subtask));
                }
                break;
            case "POST":
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);

                if (subtask.getId() == 0) {
                    try {
                        taskManager.addSubtask(subtask);
                        responseCode201(exchange, gson.toJson(Map.of("id", subtask.getId())));
                    } catch (IllegalArgumentException e) {
                        responseHasAnIntersection(exchange, subtask.getName());
                    }
                } else {
                    try {
                        taskManager.updateSubtask(subtask);
                        responseCode201(exchange);
                    } catch (IllegalArgumentException e) {
                        responseHasAnIntersection(exchange, subtask.getName());
                    }
                }
                break;
            case "DELETE":
                if (lengthPathParts == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    taskManager.removeSubtask(id);
                    responseCode201(exchange);
                }
                break;
            default:
                responseNotFound(exchange);
        }
    }
}
