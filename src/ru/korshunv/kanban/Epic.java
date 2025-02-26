package ru.korshunv.kanban;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtasks;
    private int subtaskCount = 0;

    public Epic() {
        init();
    }

    public Epic(String name, TaskStatus taskStatus) {
        super(name, taskStatus);
        init();
    }

    private void init() {
        subtasks = new HashMap<>();
        setTaskStatus(TaskStatus.NEW);
    }

    public void addSubtask(Subtask subtask) {
        subtask.setId(++subtaskCount);
        subtasks.put(subtask.getId(), subtask);
    }

    public void updateSubtask(int id, Subtask subtask) {
        if (Utils.isNumberNotZerosAndIsPositive(id)) {
            subtasks.put(id, subtask);

            if (subtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                setTaskStatus(TaskStatus.IN_PROGRESS);
            } else if (subtask.getTaskStatus() == TaskStatus.DONE) {
                int numberOfCompletedSubtasks = 0;
                for (Subtask sub : subtasks.values()) {
                    if (sub.getTaskStatus() == TaskStatus.DONE) {
                        numberOfCompletedSubtasks++;
                    }
                }

                if (numberOfCompletedSubtasks == subtasks.values().size()) {
                    setTaskStatus(TaskStatus.DONE);
                }
            }
        }
    }

    public Subtask getSubtaskOnId(int id) {
        return subtasks.get(id);
    }

    public List<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeSubtask(int id) {
        subtasks.remove(id);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }
}
