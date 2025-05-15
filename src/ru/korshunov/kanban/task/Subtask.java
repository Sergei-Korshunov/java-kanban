package ru.korshunov.kanban.task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getTaskStatus(), subtask.getStartTime(), subtask.getDuration());
        this.epicId = subtask.getEpicId();
    }

    protected Subtask(String name, String description, int id, TaskStatus taskStatus, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, id, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public static Subtask getInstance(int id, String name, TaskStatus taskStatus, String description, int epicId, LocalDateTime startTime, Duration duration) {
        return new Subtask(name, description, id, taskStatus, epicId, startTime, duration);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", taskStatus=" + taskStatus +
                '}';
    }
}
