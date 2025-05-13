package ru.korshunov.kanban.manager;

import ru.korshunov.kanban.manager.history.HistoryManager;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    private final Set<Task> prioritizedTasks;
    private final HistoryManager historyManager;
    private int taskId = 0;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        if (isIntersectionOfTasksInTime(task)) {
            throw new IllegalArgumentException("Время выполнения задачи " + '"' + task.getName() + '"' + "пересекается с сущетсвующими.");
        }

        task.setId(++taskId);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private boolean isIntersectionOfTasksInTime(Task task) {
        if (task.getStartTime() == null || task.getEndTime() == null) {
            return false;
        }

        return prioritizedTasks.stream().anyMatch(sortedTask -> {
            if (sortedTask.getId() == task.getId()) {
                return false;
            }

            LocalDateTime sortedTaskEndTime = sortedTask.getEndTime();
            LocalDateTime taskEndTime = task.getEndTime();
            return sortedTask.getStartTime().isBefore(taskEndTime) && task.getStartTime().isBefore(sortedTaskEndTime);
        });
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setId(++taskId);
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isIntersectionOfTasksInTime(subtask)) {
            throw new IllegalArgumentException("Время выполнения задачи " + '"' + subtask.getName() + '"' + " пересекается с сущетсвующими.");
        }

        Epic epicFromCollection = epics.get(subtask.getEpicId());
        if (epicFromCollection != null) {
            subtask.setId(++taskId);

            epicFromCollection.addSubtask(subtask);
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }

            checkingStatusForEpic(epicFromCollection);
            recalculatingEpicExecutionTime(epicFromCollection);
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

    private void recalculatingEpicExecutionTime(Epic epic) {
        AtomicReference<Duration> duration = new AtomicReference<>(Duration.ZERO);
        AtomicReference<LocalDateTime> startTime = new AtomicReference<>(LocalDateTime.now());
        AtomicReference<LocalDateTime> endTime = new AtomicReference<>(LocalDateTime.now());

        epic.getListOfSubtasks().stream()
                .peek(subtask -> {
                    LocalDateTime local = subtask.getStartTime();
                    if (local.isBefore(startTime.get()))
                        startTime.set(local);
                })
                .peek(subtask -> {
                    LocalDateTime local = subtask.getStartTime();
                    if (local.isAfter(endTime.get()))
                        endTime.set(local);
                })
                .peek(subtask -> {
                    Duration dur = duration.get();
                    duration.set(dur.plus(subtask.getDuration()));
                })
                .collect(Collectors.toList());

        epic.setDuration(duration.get());
        epic.setStartTime(startTime.get());
        epic.setEndTime(endTime.get());
    }

    @Override
    public void updateTask(Task task) {
        if (isIntersectionOfTasksInTime(task)) {
            throw new IllegalArgumentException("Время выполнения задачи " + '"' + task.getName() + '"' + "пересекается с сущетсвующими.");
        }

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
        if (isIntersectionOfTasksInTime(subtask)) {
            throw new IllegalArgumentException("Время выполнения задачи " + '"' + subtask.getName() + '"' + "пересекается с сущетсвующими.");
        }

        int id = subtask.getId();
        if (subtasks.containsKey(id)) {
            Subtask subtaskFromCollection = subtasks.get(id);

            if (subtask.getEpicId() == subtaskFromCollection.getEpicId()) {
                Epic epicFromCollection = epics.get(subtask.getEpicId());
                epicFromCollection.removeSubtask(subtaskFromCollection);

                epicFromCollection.addSubtask(subtask);
                subtasks.put(id, subtask);

                checkingStatusForEpic(epicFromCollection);
                recalculatingEpicExecutionTime(epicFromCollection);
           }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    protected int getTaskId() {
        return taskId;
    }

    protected void setTaskId(int taskId) {
        this.taskId = taskId;
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
            recalculatingEpicExecutionTime(epics.get(subtask.getEpicId()));
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
            recalculatingEpicExecutionTime(epic);
        }
    }
}

