package ru.korshunov.kanban.task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);

        subtasks = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getId(), epic.getTaskStatus());

        subtasks = epic.getListOfSubtasks();
    }

    protected Epic(String name, String description, int id, TaskStatus taskStatus) {
        super(name, description, id, taskStatus);

        subtasks = new ArrayList<>();
    }

    public static Epic getInstance(int id, String name, TaskStatus taskStatus, String description) {
        return new Epic(name, description, id, taskStatus);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.remove(subtask);
        }
    }

    public void clearSubtask() {
        subtasks.clear();
    }

    public List<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasks);
    }
}
