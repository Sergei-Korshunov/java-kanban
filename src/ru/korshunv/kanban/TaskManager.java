package ru.korshunv.kanban;

import java.util.*;

public class TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int taskCount = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public void addTask(Task task) {
        task.setId(++taskCount);
        tasks.put(task.getId(), task);
    }

    public void addEpic(Epic epic) {
        epic.setId(++taskCount);
        epics.put(epic.getId(), epic);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(++taskCount);
        subtasks.put(subtask.getId(), subtask);

        Epic epicFromCollection = getEpicOnId(subtask.getEpicId());
        epicFromCollection.addSubtask(subtask);

        checkingStatusForEpic(epicFromCollection);
    }

    private void checkingStatusForEpic(Epic epic) {
        int numberOfCompletedSubtasks = 0;

        for (Subtask subtask : epic.getListOfSubtasks()) {
            if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                epic.setTaskStatus(TaskStatus.IN_PROGRESS);
                return;
            } else if (subtask.getTaskStatus() == TaskStatus.DONE) {
                numberOfCompletedSubtasks++;
            }
        }

        if (numberOfCompletedSubtasks == epic.getListOfSubtasks().size()) {
            epic.setTaskStatus(TaskStatus.DONE);
        }
    }

    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic epicFromCollection = getEpicOnId(id);

            epicFromCollection.setName(epic.getName());
            epicFromCollection.setDescription(epic.getDescription());
        }
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            subtasks.put(id, subtask);

            checkingStatusForEpic(getEpicOnId(subtask.getEpicId()));
        }
    }

    public Task getTaskOnId(int id) {
        return tasks.get(id);
    }

    public Epic getEpicOnId(int id) {
        return epics.get(id);
    }

    public List<Subtask> getSubtaskOnEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            return getEpicOnId(epicId).getListOfSubtasks();
        }
        return new ArrayList<>();
    }

    public Subtask getSubtaskOnId(int id) {
        return subtasks.get(id);
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

    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = getSubtaskOnId(id);
            Epic epicFromCollection = getEpicOnId(subtask.getEpicId());

            epicFromCollection.removeSubtask(subtask);
            subtasks.remove(id);

            checkingStatusForEpic(getEpicOnId(subtask.getEpicId()));
        }
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

}

