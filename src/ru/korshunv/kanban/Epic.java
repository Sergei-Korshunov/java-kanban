package ru.korshunv.kanban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    public Epic(String name, String description, TaskStatus taskStatus) {
        super(name, description, taskStatus);

        subtasks = new ArrayList<>();
        setTaskStatus(TaskStatus.NEW);
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        if (subtask != null) {
            subtasks.remove(subtask);
        }
    }






    public List<Subtask> getListOfSubtasks() {
        return subtasks;
    }




}
