package ru.korshunov.kanban.manager;

import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;

import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    List<Task> getHistory();

    Task getTaskOnId(int id);

    Epic getEpicOnId(int id);

    List<Subtask> getSubtaskOnEpic(int epicId);

    Subtask getSubtaskOnId(int id);

    List<Task> getListOfTask();

    List<Epic> getListOfEpics();

    List<Subtask> getListOfSubtasks();

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    void clearTasks();

    void clearEpics();

    void clearSubtasks();
}
