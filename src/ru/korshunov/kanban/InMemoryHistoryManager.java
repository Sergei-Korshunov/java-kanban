package ru.korshunov.kanban;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int SIZE_OF_HISTORY = 10;

    private final List<Task> history;
    private int indexHistory = 0;

    public InMemoryHistoryManager() {
        history = new ArrayList<>(SIZE_OF_HISTORY);
    }

    @Override
    public void add(Task task) {
        if (indexHistory > SIZE_OF_HISTORY - 1) {
            history.remove(0);
            indexHistory = 0;
        }
        history.add(task);
        indexHistory++;
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
