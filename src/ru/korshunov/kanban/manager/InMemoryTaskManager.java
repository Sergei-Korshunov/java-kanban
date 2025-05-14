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
        AtomicReference<LocalDateTime> startTime = new AtomicReference<>(LocalDateTime.now());
        AtomicReference<Duration> duration = new AtomicReference<>(Duration.ZERO);
        AtomicReference<LocalDateTime> endTime = new AtomicReference<>(LocalDateTime.now());

        boolean isEmptyListOfSubtasks = epic.getListOfSubtasks().isEmpty();
        boolean isAllStartTimeNull = epic.getListOfSubtasks().stream()
                .allMatch(subtask -> subtask.getStartTime() == null);

        if (isEmptyListOfSubtasks && isAllStartTimeNull) {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
            return;
        }

        epic.getListOfSubtasks().stream()
                .peek(subtask -> {
                    LocalDateTime local = subtask.getStartTime();
                    if (local.isBefore(startTime.get()))
                        startTime.set(local);
                })
                .peek(subtask -> {
                    LocalDateTime local = subtask.getEndTime();
                    if (local.isAfter(endTime.get()))
                        endTime.set(local);
                })
                .peek(subtask -> {
                    Duration dur = duration.get();
                    duration.set(dur.plus(subtask.getDuration()));
                })
                .collect(Collectors.toList());

        epic.setStartTime(startTime.get());
        epic.setDuration(duration.get());
        epic.setEndTime(endTime.get());
    }

    @Override
    public void updateTask(Task task) {
        int id = task.getId();
        if (tasks.containsKey(id)) {
            if (isIntersectionOfTasksInTime(task)) {
                throw new IllegalArgumentException("Время выполнения задачи " + '"' + task.getName() + '"' + "пересекается с сущетсвующими.");
            }

            prioritizedTasks.remove(tasks.get(id));
            tasks.put(task.getId(), task);

            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
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
            if (isIntersectionOfTasksInTime(subtask)) {
                throw new IllegalArgumentException("Время выполнения задачи " + '"' + subtask.getName() + '"' + "пересекается с сущетсвующими.");
            }
            Subtask subtaskFromCollection = subtasks.get(id);
            prioritizedTasks.remove(subtaskFromCollection);

            if (subtask.getEpicId() == subtaskFromCollection.getEpicId()) {
                Epic epicFromCollection = epics.get(subtask.getEpicId());
                epicFromCollection.removeSubtask(subtaskFromCollection);

                epicFromCollection.addSubtask(subtask);
                subtasks.put(id, subtask);

                if (subtask.getStartTime() != null) {
                    prioritizedTasks.add(subtask);
                }

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
        prioritizedTasks.remove(tasks.get(id));
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epicFromCollection = epics.get(id);
            for (Subtask subtask : epicFromCollection.getListOfSubtasks()) {
                prioritizedTasks.remove(subtasks.get(subtask.getId()));
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
            }
            prioritizedTasks.remove(epicFromCollection);
            historyManager.remove(id);
            epics.remove(id);
        }
    }

    @Override
    public void removeSubtask(int id) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epicFromCollection = epics.get(subtask.getEpicId());

            epicFromCollection.removeSubtask(subtask);
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
            subtasks.remove(id);

            checkingStatusForEpic(epics.get(subtask.getEpicId()));
            recalculatingEpicExecutionTime(epics.get(subtask.getEpicId()));
        }
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
        }
        clearSubtaskFromHistoryAndPrioritizedTasks();
        epics.clear();
        subtasks.clear();
    }

    private void clearSubtaskFromHistoryAndPrioritizedTasks() {
        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        }
    }

    @Override
    public void clearSubtasks() {
        clearSubtaskFromHistoryAndPrioritizedTasks();
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearSubtask();
            checkingStatusForEpic(epic);
            recalculatingEpicExecutionTime(epic);
        }
    }
}

