package ru.korshunv.kanban;

import java.util.*;
import static ru.korshunv.kanban.Utils.*;

public class TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private int taskCount = 0;
    private int epicCount = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public void addTask(Task task) {
        task.setId(++taskCount);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(++epicCount);
        epics.put(epic.getId(), epic);
    }

    public void updateTask(int id, Task task) {
        if (isNumberNotZerosAndIsPositive(id) && tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    public void updateEpic(int id, Epic epic) {
        if (isNumberNotZerosAndIsPositive(id) && epics.containsKey(id)) {
            epics.put(id, epic);
        }
    }

    public Task getTaskOnId(int id) {
        return tasks.get(id);
    }

    public Epic getEpicOnId(int id) {
        return epics.get(id);
    }

    public List<Task> getListOfTask() {
        return new ArrayList<>(tasks.values());
    }

    public List<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        epics.remove(id);
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }
}

