package ru.korshunov.kanban.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.korshunov.kanban.manager.history.HistoryManager;
import ru.korshunov.kanban.manager.history.InMemoryHistoryManager;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class InMemoryHistoryManagerTest {

    protected HistoryManager historyManager;

    @BeforeEach
    void installInstance() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void checkingHistory() {
        Task taskNew = new Task("Новыя задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskNew.setId(1);
        historyManager.add(taskNew);

        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size());
    }

    @Test
    void checkingForDuplication() {
        Task taskNew = new Task("Новыя задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskNew.setId(1);

        historyManager.add(taskNew);
        historyManager.add(taskNew);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
    }

    @Test
    void removeTaskFromBeginning() {
        Task taskNew1 = new Task("Новыя задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskNew1.setId(1);
        historyManager.add(taskNew1);

        Task taskNew2 = new Task("Новыя задача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.now(), Duration.of(21, ChronoUnit.MINUTES));
        taskNew2.setId(2);
        historyManager.add(taskNew2);

        Task taskNew3 = new Task("Новыя задача 3", "Описание 3", TaskStatus.NEW, LocalDateTime.now(), Duration.of(22, ChronoUnit.MINUTES));
        taskNew3.setId(3);
        historyManager.add(taskNew3);

        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(taskNew2, history.get(0));
        assertEquals(taskNew3, history.get(1));
    }

    @Test
    void removeTaskFromMiddle() {
        Task taskNew1 = new Task("Новыя задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskNew1.setId(1);
        historyManager.add(taskNew1);

        Task taskNew2 = new Task("Новыя задача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.now(), Duration.of(21, ChronoUnit.MINUTES));
        taskNew2.setId(2);
        historyManager.add(taskNew2);

        Task taskNew3 = new Task("Новыя задача 3", "Описание 3", TaskStatus.NEW, LocalDateTime.now(), Duration.of(22, ChronoUnit.MINUTES));
        taskNew3.setId(3);
        historyManager.add(taskNew3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(taskNew1, history.get(0));
        assertEquals(taskNew3, history.get(1));
    }

    @Test
    void removeTaskFromEnd() {
        Task taskNew1 = new Task("Новыя задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskNew1.setId(1);
        historyManager.add(taskNew1);

        Task taskNew2 = new Task("Новыя задача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.now(), Duration.of(21, ChronoUnit.MINUTES));
        taskNew2.setId(2);
        historyManager.add(taskNew2);

        Task taskNew3 = new Task("Новыя задача 3", "Описание 3", TaskStatus.NEW, LocalDateTime.now(), Duration.of(22, ChronoUnit.MINUTES));
        taskNew3.setId(3);
        historyManager.add(taskNew3);

        historyManager.remove(3);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(taskNew1, history.get(0));
        assertEquals(taskNew2, history.get(1));
    }

    @Test
    void checkingOperationHistory() {
        Task taskNew1 = new Task("Новыя задача 1", "", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskNew1.setId(1);
        historyManager.add(taskNew1);

        // Первая большая задача
        Epic epicNew1 = new Epic("Поменять пробитое колесо на велосипеде.", "");
        epicNew1.setId(2);
        historyManager.add(epicNew1);

        Subtask subtask1 = new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epicNew1.getId(), LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        subtask1.setId(3);
        Subtask subtask2 = new Subtask("Поменять колесо.", "", TaskStatus.NEW, epicNew1.getId(), LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        subtask2.setId(4);
        historyManager.add(subtask1);
        historyManager.add(subtask2);

        // Вторая большая задача
        Epic epicNew2 = new Epic("Сходить в магазин.", "");
        epicNew2.setId(5);
        historyManager.add(epicNew2);

        Task taskNew2 = new Task("Новыя задача 2", "", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskNew2.setId(6);
        historyManager.add(taskNew2);

        Task taskNew3 = new Task("Новыя задача 3", "", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskNew3.setId(7);
        historyManager.add(taskNew3);


        List<Task> history = historyManager.getHistory();
        printHistory(history);

        historyManager.add(taskNew1);

        history = historyManager.getHistory();
        printHistory(history);
        assertEquals(taskNew1, history.get(history.size() - 1));

        historyManager.remove(epicNew2.getId());
        historyManager.remove(taskNew2.getId());

        history = historyManager.getHistory();
        printHistory(history);

        for (Task task : history) {
            assertNotEquals(epicNew2, task);
        }

        for (Task task : history) {
            assertNotEquals(taskNew2, task);
        }
    }

    private void printHistory(List<Task> history) {
        System.out.println("------------------------------------");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i + 1) + ". -> " + history.get(i));
        }
        System.out.println();
    }
}
