package ru.korshunov.kanban.test;

import ru.korshunov.kanban.manager.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return taskManager = new InMemoryTaskManager();
    }

}