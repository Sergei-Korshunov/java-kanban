package ru.korshunov.kanban.task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus taskStatus, int epicId) {
        super(name, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(Subtask subtask) {
        super(subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getTaskStatus());
        this.epicId = subtask.getEpicId();
    }

    protected Subtask(String name, String description, int id, TaskStatus taskStatus, int epicId) {
        super(name, description, id, taskStatus);
        this.epicId = epicId;
    }

    public static Subtask getInstance(int id, String name, TaskStatus taskStatus, String description, int epicId) {
        return new Subtask(name, description, id, taskStatus, epicId);
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
