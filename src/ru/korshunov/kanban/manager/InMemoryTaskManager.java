package ru.korshunov.kanban.manager;

import ru.korshunov.kanban.manager.history.HistoryManager;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;
    private int taskCount = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        task.setId(++taskCount);
        tasks.put(task.getId(), task);
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++taskCount);
        epics.put(epic.getId(), epic);
    }

    @Override
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

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            tasks.put(id, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (epics.containsKey(id)) {
            Epic epicFromCollection = epics.get(id);

            epicFromCollection.setName(epic.getName());
            epicFromCollection.setDescription(epic.getDescription());
        }
    }

    @Override
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Task getTaskOnId(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }

        return task;
    }

    @Override
    public Epic getEpicOnId(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }

        return epic;
    }

    @Override
    public List<Subtask> getSubtaskOnEpic(int epicId) {
        if (epics.containsKey(epicId)) {
            return epics.get(epicId).getListOfSubtasks();
        }

        return new ArrayList<>();
    }

    @Override
    public Subtask getSubtaskOnId(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }

        return subtask;
    }

    @Override
    public List<Task> getListOfTask() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getListOfEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getListOfSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeTask(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epicFromCollection = epics.get(id);
            for (Subtask subtask : epicFromCollection.getListOfSubtasks()) {
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epicFromCollection = epics.get(subtask.getEpicId());

            epicFromCollection.removeSubtask(subtask);
            historyManager.remove(id);
            subtasks.remove(id);

            checkingStatusForEpic(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        clearSubtaskFromHistory();
        epics.clear();
        subtasks.clear();
    }

    private void clearSubtaskFromHistory() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
    }

    @Override
    public void clearSubtasks() {
        clearSubtaskFromHistory();
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            checkingStatusForEpic(epic);
        }
    }
}

