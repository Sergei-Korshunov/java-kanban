package ru.korshunov.kanban.httpserver;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;

import ru.korshunov.kanban.httpserver.handler.*;
import ru.korshunov.kanban.manager.Managers;
import ru.korshunov.kanban.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpKanbanServer {

    private static final int PORT = 8080;
    private static Gson gson = new Gson();

    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpKanbanServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        registerContext();
    }

    private void registerContext() {
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        httpServer.start();
        System.out.println("Сервер Kanban запущен.");
        System.out.println("Адресс сервера: 127.0.0.1");
        System.out.println("Порт сервера: " + httpServer.getAddress().getPort());
    }


    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            System.out.println("Сервер Kanban остановлен.");
        }
    }

    public static void main(String[] args) {
        try {
            new HttpKanbanServer(Managers.getDefaultTaskManager()).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
