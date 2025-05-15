package ru.korshunov.kanban.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static Epic epic;
    private Subtask subtask;

    @BeforeAll
    static void createEpic() {
        epic = new Epic("Поменять пробитое колесо на велосипеде.", "");
    }

    @BeforeEach
    void addSubtaskInTheEpicList() {
        subtask = new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epic.getId(), null, null);
        epic.addSubtask(subtask);
    }

    @Test
    void checkInTheList() {
        assertEquals(1, epic.getListOfSubtasks().size());
    }

    @Test
    void removeSubtaskInTheEpicList() {
        epic.removeSubtask(subtask);

        assertEquals(0, epic.getListOfSubtasks().size());
    }

    @Test
    void clearAllListInTheSubtaskInEpic() {
        epic.clearSubtask();

        assertEquals(0, epic.getListOfSubtasks().size());
    }

}