package ru.korshunov.kanban.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.korshunov.kanban.httpserver.HttpKanbanServer;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.task.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TasksHandler extends BaseHandler implements HttpHandler {

    private final TaskManager taskManager;
    private Gson gson = HttpKanbanServer.getGson();

    public TasksHandler(TaskManager taskManager) {
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
                    List<Task> tasks = taskManager.getListOfTask();
                    if (tasks == null) {
                        responseNotFound(exchange);
                        return;
                    }
                    responseOk(exchange, gson.toJson(tasks));

                } else if (lengthPathParts == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    Task task = taskManager.getTaskOnId(id);
                    if (task == null) {
                        responseNotFound(exchange);
                        return;
                    }
                    responseOk(exchange, gson.toJson(task));
                }
                break;
            case "POST":
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);

                if (task.getId() == 0) {
                    try {
                        taskManager.addTask(task);
                        responseCode201(exchange, gson.toJson(Map.of("id", task.getId())));
                    } catch (IllegalArgumentException e) {
                        responseHasAnIntersection(exchange, task.getName());
                    }
                } else {
                    try {
                        taskManager.updateTask(task);
                        responseCode201(exchange);
                    } catch (IllegalArgumentException e) {
                        responseHasAnIntersection(exchange, task.getName());
                    }
                }
                break;
            case "DELETE":
                if (lengthPathParts == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    taskManager.removeTask(id);
                    responseCode201(exchange);
                }
                break;
            default:
                responseNotFound(exchange);
        }
    }
}
