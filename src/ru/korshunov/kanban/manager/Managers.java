package ru.korshunov.kanban.manager;

import ru.korshunov.kanban.manager.history.HistoryManager;
import ru.korshunov.kanban.manager.history.InMemoryHistoryManager;

public class Managers {

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
