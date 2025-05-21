package ru.korshunov.kanban.test;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.korshunov.kanban.httpserver.HttpKanbanServer;
import ru.korshunov.kanban.manager.InMemoryTaskManager;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    private final TaskManager taskManager = new InMemoryTaskManager();
    private final HttpKanbanServer kanbanServer = new HttpKanbanServer(taskManager);
    private final Gson gson = HttpKanbanServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        taskManager.clearTasks();
        taskManager.clearSubtasks();
        taskManager.clearEpics();
        kanbanServer.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    public void shutDown() {
        kanbanServer.stop();
    }

    @Test
    @DisplayName("GET .../tasks")
    public void getListTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Здача 1", "Описание 1", TaskStatus.NEW,  LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Здача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.now().plusMinutes(11), Duration.ofMinutes(20));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TypeTokenForTaskList().getType());
        assertEquals(2, tasksFromResponse.size(), "Некорректное количество задач");
    }

    @Test
    @DisplayName("GET .../tasks/1")
    public void getTasksById() throws IOException, InterruptedException {
        Task task1 = new Task("Здача 1", "Описание 1", TaskStatus.NEW,  LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Здача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.now().plusMinutes(11), Duration.ofMinutes(20));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(responseTask, "Задача не получена");
        assertEquals(task1, responseTask, "Задачи не совпадают");
    }

    @Test
    @DisplayName("POST .../tasks")
    public void addTask() throws IOException, InterruptedException {
        Task task = new Task("Здача 1", "Описание 1",
                TaskStatus.NEW,  LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getListOfTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Здача 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        assertEquals(Map.of("id", 1), gson.fromJson(response.body(), new TypeTokenHashMap().getType()));
    }

    @Test
    @DisplayName("POST .../tasks")
    public void updateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Здача 1", "Описание 1", TaskStatus.NEW,  LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Здача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.now().plusMinutes(11), Duration.ofMinutes(20));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        String descriptionForTest = "Описание для обновления задачи №1";

        assertNotEquals(taskManager.getTaskOnId(1).getDescription(), descriptionForTest);

        String taskJson = "\t{\n" +
                "\t\t\"name\": \"Здача 1\",\n" +
                "\t\t\"description\": \"Описание для обновления задачи №1\",\n" +
                "\t\t\"id\": 1,\n" +
                "\t\t\"taskStatus\": \"NEW\",\n" +
                "\t\t\"startTime\": \"2025-05-21T08:51:33.019775200\",\n" +
                "\t\t\"duration\": 600\n" +
                "\t}";


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertEquals(descriptionForTest, taskManager.getTaskOnId(1).getDescription(), "Описания не совподают");
    }


    @Test
    @DisplayName("DELETE .../tasks/1")
    public void deleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("Здача 1", "Описание 1", TaskStatus.NEW,  LocalDateTime.now(), Duration.ofMinutes(10));
        Task task2 = new Task("Здача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.now().plusMinutes(11), Duration.ofMinutes(20));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(url)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        assertEquals(1, taskManager.getListOfTask().size(), "Размер списка не совпадает");
        assertEquals(task2, taskManager.getListOfTask().get(0), "Задачи не совпадают");
        assertNotEquals(task1, taskManager.getListOfTask().get(0), "Задачи совпадают");
    }
}