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
