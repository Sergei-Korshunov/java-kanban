package ru.korshunov.kanban.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.korshunov.kanban.manager.InMemoryTaskManager;
import ru.korshunov.kanban.manager.Managers;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private static InMemoryTaskManager memoryTaskManager;

    @BeforeEach
    void createInMemoryTaskManager() {
        memoryTaskManager = new InMemoryTaskManager();
    }

    @Test
    void checkingTheInstancesOfTheInheritorsOfTheTaskClassForIdEquality() {
        // Task
        Task taskNew = new Task("Новыя задача 1", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew);

        int idTask = taskNew.getId();
        Task task = memoryTaskManager.getTaskOnId(idTask);

        assertNotNull(task, "Задача не найдена.");
        assertEquals(taskNew, task, "Не совпадение задач.");

        // Epic
        Epic epicNew = new Epic("Новыя задача 2", "");
        memoryTaskManager.addEpic(epicNew);

        int idEpic = epicNew.getId();
        Epic epic = memoryTaskManager.getEpicOnId(idEpic);

        assertNotNull(epic, "Задача не найдена.");
        assertEquals(epicNew, epic, "Не совпадение задач.");

        // Subtask
        Subtask subtaskNew = new Subtask("Новыя задача 3", "", TaskStatus.NEW, epicNew.getId());
        memoryTaskManager.addSubtask(subtaskNew);

        int idSubtask = subtaskNew.getId();
        Subtask subtask = memoryTaskManager.getSubtaskOnId(idSubtask);

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(subtaskNew, subtask, "Не совпадение задач.");
    }

    @Test
    void checkEpicStatus() {
        Epic epic = new Epic("Поменять пробитое колесо на велосипеде.", "");
        memoryTaskManager.addEpic(epic);

        Subtask s1 = new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epic.getId());
        Subtask s2 = new Subtask("Поменять колесо.", "", TaskStatus.NEW, epic.getId());
        memoryTaskManager.addSubtask(s1);
        memoryTaskManager.addSubtask(s2);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        s1.setTaskStatus(TaskStatus.IN_PROGRESS);
        memoryTaskManager.updateSubtask(s1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        s2.setTaskStatus(TaskStatus.IN_PROGRESS);
        memoryTaskManager.updateSubtask(s2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        s1.setTaskStatus(TaskStatus.DONE);
        memoryTaskManager.updateSubtask(s1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        s2.setTaskStatus(TaskStatus.DONE);
        memoryTaskManager.updateSubtask(s2);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());

        memoryTaskManager.clearSubtasks();

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    void initManager() {
        assertNotNull(Managers.getDefaultTaskManager(), "Экземпляр менеджера не создан.");
        assertNotNull(Managers.getDefaultHistory(), "Экземпляр истории не создан.");
    }

    @Test
    void checkingForIdConflict() {
        Task taskNew = new Task("Новыя задача 1", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew);

        int idTask = taskNew.getId();
        assertEquals(taskNew, memoryTaskManager.getTaskOnId(idTask));

        taskNew.setId(2);
        assertNull(memoryTaskManager.getTaskOnId(2));
    }

    @Test
    void checkingForImmutabilityOfTheTask() {
        Task taskNew1 = new Task("Новыя задача 1", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew1);

        assertEquals(taskNew1.getName(), "Новыя задача 1");
        assertEquals(taskNew1.getDescription(), "");
        assertEquals(taskNew1.getTaskStatus(), TaskStatus.NEW);
    }

    @Test
    void checkingHistory() {
        Task taskNew = new Task("Новыя задача 1", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew);

        memoryTaskManager.getTaskOnId(taskNew.getId());
        List<Task> history = memoryTaskManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size());
    }

    @Test
    void checkingOperationHistory() {
        Task taskNew1 = new Task("Новыя задача 1", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew1);

        // Первая большая задача
        Epic epicNew1 = new Epic("Поменять пробитое колесо на велосипеде.", "");
        memoryTaskManager.addEpic(epicNew1);

        Subtask subtask1 = new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epicNew1.getId());
        Subtask subtask2 = new Subtask("Поменять колесо.", "", TaskStatus.NEW, epicNew1.getId());
        memoryTaskManager.addSubtask(subtask1);
        memoryTaskManager.addSubtask(subtask2);

        // Вторая большая задача
        Epic epicNew2 = new Epic("Сходить в магазин.", "");
        memoryTaskManager.addEpic(epicNew2);

        Task taskNew2 = new Task("Новыя задача 2", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew2);

        Task taskNew3 = new Task("Новыя задача 3", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew3);

        memoryTaskManager.getTaskOnId(taskNew1.getId());
        memoryTaskManager.getEpicOnId(epicNew1.getId());
        memoryTaskManager.getEpicOnId(epicNew2.getId());
        memoryTaskManager.getTaskOnId(taskNew2.getId());
        memoryTaskManager.getTaskOnId(taskNew3.getId());

        List<Task> history = memoryTaskManager.getHistory();
        printHistory(history);

        memoryTaskManager.getTaskOnId(taskNew1.getId());

        history = memoryTaskManager.getHistory();
        printHistory(history);
        assertEquals(taskNew1, history.get(history.size() - 1));

        memoryTaskManager.removeEpic(epicNew2.getId());
        memoryTaskManager.removeTask(taskNew2.getId());

        history = memoryTaskManager.getHistory();
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