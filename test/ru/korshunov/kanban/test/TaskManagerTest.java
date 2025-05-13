package ru.korshunov.kanban.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.korshunov.kanban.manager.Managers;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager() throws Exception;

    @BeforeEach
    public void installInstance() throws Exception {
        taskManager = createTaskManager();
    }

    @Test
    void createNewTask() {
        Task taskNew = new Task("Новыя задача #1", "Описание #1", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addTask(taskNew);

        int idTask = taskNew.getId();
        Task task = taskManager.getTaskOnId(idTask);

        assertNotNull(task, "Задача не найдена.");
        assertEquals(taskNew, task, "Не совпадение задач.");
    }

    @Test
    void createNewEpic() {
        Epic epicNew = new Epic("Новыя задача #2", "Описание 2");
        taskManager.addEpic(epicNew);

        int idEpic = epicNew.getId();
        Epic epic = taskManager.getEpicOnId(idEpic);

        assertNotNull(epic, "Задача не найдена.");
        assertEquals(epicNew, epic, "Не совпадение задач.");
    }

    @Test
    void createNewSubtask() {
        Epic epicNew = new Epic("Для проверки #1", "Описание для проверки #1");
        taskManager.addEpic(epicNew);

        Subtask subtaskNew = new Subtask("Новыя задача #3", "Описание #3", TaskStatus.NEW, epicNew.getId(), LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addSubtask(subtaskNew);

        int idSubtask = subtaskNew.getId();
        Subtask subtask = taskManager.getSubtaskOnId(idSubtask);

        assertNotNull(subtask, "Задача не найдена.");
        assertEquals(subtaskNew, subtask, "Не совпадение задач.");
    }

    @Test
    void checkEpicStatus() {
        Epic epic = new Epic("Поменять пробитое колесо на велосипеде.", "");
        taskManager.addEpic(epic);

        Subtask s1 = new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epic.getId(), LocalDateTime.of(2025,5,13,16,15), Duration.of(20, ChronoUnit.MINUTES));
        Subtask s2 = new Subtask("Поменять колесо.", "", TaskStatus.NEW, epic.getId(), LocalDateTime.of(2025,5,13,15,15), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addSubtask(s1);
        taskManager.addSubtask(s2);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        s1.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(s1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        s2.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(s2);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        s1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(s1);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());

        s2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(s2);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());

        taskManager.clearSubtasks();

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    void initManager() {
        assertNotNull(Managers.getDefaultTaskManager(), "Экземпляр менеджера не создан.");
        assertNotNull(Managers.getDefaultHistory(), "Экземпляр истории не создан.");
    }

    @Test
    void checkingForIdConflict() {
        Task taskNew = new Task("Новыя задача 1", "", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addTask(taskNew);

        int idTask = taskNew.getId();
        assertEquals(taskNew, taskManager.getTaskOnId(idTask));

        taskNew.setId(2);
        assertNull(taskManager.getTaskOnId(2));
    }

    @Test
    void checkingForImmutabilityOfTheTask() {
        Task taskNew1 = new Task("Новыя задача 1", "", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addTask(taskNew1);

        assertEquals(taskNew1.getName(), "Новыя задача 1");
        assertEquals(taskNew1.getDescription(), "");
        assertEquals(taskNew1.getTaskStatus(), TaskStatus.NEW);
    }

    @Test
    void checkingForIntersection() {
        Task taskNew1 = new Task("Новыя задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        Task taskNew2 = new Task("Новыя задача 2", "Описание 2", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addTask(taskNew1);


        assertThrows(IllegalArgumentException.class, () -> {
            taskManager.addTask(taskNew2);
        }, "Задачи пересекаются.");

    }

}
