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
        Epic epicFromCollection = epics.get(subtask.getEpicId());
        if (epicFromCollection != null) {
            subtask.setId(++taskCount);

            epicFromCollection.addSubtask(subtask);
            subtasks.put(subtask.getId(), subtask);

            checkingStatusForEpic(epicFromCollection);
        }
    }

    private void checkingStatusForEpic(Epic epic) {
        if (epic.getListOfSubtasks().isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        int numberOfNewSubtasks = 0;
        int numberOfCompletedSubtasks = 0;
        for (Subtask subtask : epic.getListOfSubtasks()) {
            if (subtask.getTaskStatus() == TaskStatus.NEW) {
                numberOfNewSubtasks++;
            } else if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                epic.setTaskStatus(TaskStatus.IN_PROGRESS);
                return;
            } else if (subtask.getTaskStatus() == TaskStatus.DONE) {
                numberOfCompletedSubtasks++;
            }
        }

        if (numberOfNewSubtasks == epic.getListOfSubtasks().size()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
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
            Epic epicFromCollection = epics.get(id);

            epicFromCollection.setName(epic.getName());
            epicFromCollection.setDescription(epic.getDescription());
        }
    }

    public void updateSubtask(Subtask subtask) {
        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            Subtask subtaskFromCollection = subtasks.get(id);

            if (subtask.getEpicId() == subtaskFromCollection.getEpicId()) {
                Epic epicFromCollection = epics.get(subtask.getEpicId());
                epicFromCollection.removeSubtask(subtaskFromCollection);

                epicFromCollection.addSubtask(subtask);
                subtasks.put(id, subtask);

                checkingStatusForEpic(epicFromCollection);
           }
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
            return epics.get(epicId).getListOfSubtasks();
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

    public List<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epicFromCollection = epics.get(id);
            for (Subtask subtask : epicFromCollection.getListOfSubtasks()) {
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
        }
    }

    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epicFromCollection = epics.get(subtask.getEpicId());

            epicFromCollection.removeSubtask(subtask);
            subtasks.remove(id);

            checkingStatusForEpic(epics.get(subtask.getEpicId()));
        }
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            checkingStatusForEpic(epic);
        }
    }
}

