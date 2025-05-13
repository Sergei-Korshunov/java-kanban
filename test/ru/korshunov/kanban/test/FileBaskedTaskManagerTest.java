package ru.korshunov.kanban.test;

import org.junit.jupiter.api.Test;
import ru.korshunov.kanban.manager.FileBackedTaskManager;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBaskedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected Path path = Path.of("task.txt");

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return taskManager = new FileBackedTaskManager(path);
    }

    @Test
    void saveAndLoadTaskFromFile() {
        Task task = new Task("Новыя задача 1", "Описание 1", TaskStatus.NEW, LocalDateTime.of(2025, 5, 13, 15, 15), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addTask(task);
        Epic epic = new Epic("Новыя задача 2", "Описание 1");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Новыя задача 3", "Описание 1", TaskStatus.NEW, epic.getId(), LocalDateTime.of(2025, 5, 13, 16, 15), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addSubtask(subtask);

        FileBackedTaskManager fbtm = FileBackedTaskManager.loadFromFile(path);

        assertEquals(1, fbtm.getListOfTask().size());
        assertEquals(1, fbtm.getListOfEpics().size());
        assertEquals(1, fbtm.getListOfSubtasks().size());
        assertEquals(task, fbtm.getTaskOnId(1));
        assertEquals(epic, fbtm.getEpicOnId(2));
        assertEquals(subtask, fbtm.getSubtaskOnId(3));
    }
}
