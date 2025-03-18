package ru.korshunov.kanban.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.korshunov.kanban.*;

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
    void checkingHistoryForOverflow() {
        Task taskNew1 = new Task("Новыя задача 1", "", TaskStatus.NEW);
        Task taskNew2 = new Task("Новыя задача 2", "", TaskStatus.NEW);
        Task taskNew3 = new Task("Новыя задача 3", "", TaskStatus.NEW);
        Task taskNew4 = new Task("Новыя задача 4", "", TaskStatus.NEW);
        Task taskNew5 = new Task("Новыя задача 5", "", TaskStatus.NEW);
        Task taskNew6 = new Task("Новыя задача 6", "", TaskStatus.NEW);
        Task taskNew7 = new Task("Новыя задача 7", "", TaskStatus.NEW);
        Task taskNew8 = new Task("Новыя задача 8", "", TaskStatus.NEW);
        Task taskNew9 = new Task("Новыя задача 9", "", TaskStatus.NEW);
        Task taskNew10 = new Task("Новыя задача 10", "", TaskStatus.NEW);

        memoryTaskManager.addTask(taskNew1);
        memoryTaskManager.addTask(taskNew2);
        memoryTaskManager.addTask(taskNew3);
        memoryTaskManager.addTask(taskNew4);
        memoryTaskManager.addTask(taskNew5);
        memoryTaskManager.addTask(taskNew6);
        memoryTaskManager.addTask(taskNew7);
        memoryTaskManager.addTask(taskNew8);
        memoryTaskManager.addTask(taskNew9);
        memoryTaskManager.addTask(taskNew10);

        memoryTaskManager.getTaskOnId(taskNew1.getId());
        memoryTaskManager.getTaskOnId(taskNew2.getId());
        memoryTaskManager.getTaskOnId(taskNew3.getId());
        memoryTaskManager.getTaskOnId(taskNew4.getId());
        Task task5 = memoryTaskManager.getTaskOnId(taskNew5.getId());
        memoryTaskManager.getTaskOnId(taskNew6.getId());
        memoryTaskManager.getTaskOnId(taskNew7.getId());
        memoryTaskManager.getTaskOnId(taskNew8.getId());
        memoryTaskManager.getTaskOnId(taskNew9.getId());
        memoryTaskManager.getTaskOnId(taskNew10.getId());

        List<Task> history = memoryTaskManager.getHistory();

        assertEquals(taskNew5, task5);

        Task taskNew11 = new Task("Новыя задача 11", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew11);

        Task task11 = memoryTaskManager.getTaskOnId(taskNew11.getId());

        assertEquals(task11, history.get(history.size() - 1));
        assertNotEquals(taskNew1, history.get(0));

        Task taskNew12 = new Task("Новыя задача 12", "", TaskStatus.NEW);
        memoryTaskManager.addTask(taskNew12);

        Task task12 = memoryTaskManager.getTaskOnId(taskNew12.getId());

        assertEquals(task12, history.get(history.size() - 1));
    }
}