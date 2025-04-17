package ru.korshunov.kanban.manager.history;

import ru.korshunov.kanban.task.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
