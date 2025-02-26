package ru.korshunv.kanban;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private TaskStatus taskStatus;

    public Task() {

    }

    public Task(String name, TaskStatus taskStatus) {
        this.name = name;
        this.taskStatus = taskStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (this == o) return true;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task {" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", uuid=" + id +
                ", taskStatus=" + taskStatus +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }
}
