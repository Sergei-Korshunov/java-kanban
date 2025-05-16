package ru.korshunov.kanban.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import ru.korshunov.kanban.httpserver.HttpKanbanServer;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.task.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHandler implements HttpHandler {

    private final TaskManager taskManager;
    private Gson gson = HttpKanbanServer.getGson();

    public EpicsHandler(TaskManager taskManager) {
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
                    List<Epic> epics = taskManager.getListOfEpics();
                    if (epics == null) {
                        responseNotFound(exchange);
                        return;
                    }
                    responseOk(exchange, gson.toJson(epics));

                } else if (lengthPathParts == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    Epic epic = taskManager.getEpicOnId(id);
                    if (epic == null) {
                        responseNotFound(exchange);
                        return;
                    }
                    responseOk(exchange, gson.toJson(epic));
                } else if (lengthPathParts == 4 && pathParts[3].equals("subtask")) {
                    int id = Integer.parseInt(pathParts[2]);
                    Epic epic = taskManager.getEpicOnId(id);
                    if (epic == null) {
                        responseNotFound(exchange);
                        return;
                    }
                    responseOk(exchange, gson.toJson(epic.getListOfSubtasks()));
                }
                break;
            case "POST":
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);

                if (epic.getId() == 0) {
                    taskManager.addEpic(epic);
                    responseCode201(exchange);
                } else {
                    try {
                        taskManager.updateEpic(epic);
                        responseCode201(exchange);
                    } catch (IllegalArgumentException e) {
                        responseHasAnIntersection(exchange, epic.getName());
                    }
                }
                break;
            case "DELETE":
                if (lengthPathParts == 3) {
                    int id = Integer.parseInt(pathParts[2]);
                    taskManager.removeEpic(id);
                    responseCode201(exchange);
                }
                break;
            default:
                responseNotFound(exchange);
        }
    }
}
