package ru.korshunov.kanban.manager.history;

import ru.korshunov.kanban.task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int SIZE_OF_HISTORY = 10;

    private final List<Task> history;
    private int indexHistory = 0;

    public InMemoryHistoryManager() {
        history = new ArrayList<>(SIZE_OF_HISTORY);
    }

    @Override
    public void add(Task task) {
        if (indexHistory == SIZE_OF_HISTORY) {
            history.remove(0);
            indexHistory--;
        }
        history.add(task);
        indexHistory++;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
