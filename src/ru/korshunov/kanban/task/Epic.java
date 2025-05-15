package ru.korshunov.kanban.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW, null, Duration.ZERO);

        subtasks = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getId(), epic.getTaskStatus(), epic.getStartTime(), epic.getDuration());

        subtasks = epic.getListOfSubtasks();
    }

    protected Epic(String name, String description, int id, TaskStatus taskStatus) {
        super(name, description, id, taskStatus, null, Duration.ZERO);

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

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
