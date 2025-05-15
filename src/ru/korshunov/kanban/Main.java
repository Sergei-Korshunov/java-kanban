package ru.korshunov.kanban;

import ru.korshunov.kanban.manager.FileBackedTaskManager;
import ru.korshunov.kanban.manager.Managers;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class Main {
    private static final Path PATH_TO_TASK_FILE = Path.of("task.txt");
    private static final TaskManager taskManager = Managers.getDefaultTaskManager();
    private static final FileBackedTaskManager fileTaskManager = new FileBackedTaskManager(PATH_TO_TASK_FILE);

    public static void main(String[] args) {
        runTestForFileTaskManagerSave();
        runTestForTaskManager();
    }

    private static void runTestForTaskManager() {
        // Первая малая задача
        taskManager.addTask(new Task("Убрать крвартиру.", "", TaskStatus.NEW, LocalDateTime.of(2025, 5, 13, 12, 15), Duration.of(20, ChronoUnit.MINUTES)));
        taskManager.addTask(new Task("Поиграть с кошкой.", "", TaskStatus.NEW, LocalDateTime.of(2025, 5, 13, 13, 15), Duration.of(20, ChronoUnit.MINUTES)));

        // Первая большая задача
        Epic epic1 = new Epic("Поменять пробитое колесо на велосипеде.", "");
        taskManager.addEpic(epic1);

        Subtask s1 = new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epic1.getId(), LocalDateTime.of(2025, 5, 13, 14, 15), Duration.of(20, ChronoUnit.MINUTES));
        Subtask s2 = new Subtask("Поменять колесо.", "", TaskStatus.NEW, epic1.getId(), LocalDateTime.of(2025, 5, 13, 15, 15), Duration.of(20, ChronoUnit.MINUTES));
        taskManager.addSubtask(s1);
        taskManager.addSubtask(s2);

        // Вторая большая задача
        Epic epic2 = new Epic("Сходить в магазин.", "");
        taskManager.addEpic(epic2);

        taskManager.addSubtask(new Subtask("Купить товары по списку.", "", TaskStatus.NEW, epic2.getId(), LocalDateTime.of(2025, 5, 13, 16, 15), Duration.of(20, ChronoUnit.MINUTES)));

        printAllTasks();

        // Обновляем данные
        taskManager.getTaskOnId(1);
        taskManager.getTaskOnId(2);
        taskManager.getEpicOnId(6);

        System.out.println("History:");
        for (int i = 0; i < taskManager.getHistory().size(); i++) {
            System.out.println((i + 1) + " -> " + taskManager.getHistory().get(i));
        }

        printAllTasks();
    }

    private static void printAllTasks() {
        System.out.println("----------------------------------");
        // Выводим небольшие задачи
        List<Task> tasks = taskManager.getListOfTask();
        System.out.println("Список небольших задач");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i).getName() + " status - " + tasks.get(i).getTaskStatus());
        }
        System.out.println();

        // Выводим большие задачи
        System.out.println("Список больших задач");
        System.out.println("Список подзадач: " + taskManager.getListOfSubtasks());
        List<Epic> epics = taskManager.getListOfEpics();
        for (int i = 0; i < epics.size(); i++) {
            Epic epic = epics.get(i);
            System.out.println((i + 1) + ". " + epic.getName() + " status - " + epic.getTaskStatus());
            System.out.print('\t');

            List<Subtask> subtasks = epic.getListOfSubtasks();
            for (int j = 0; j < subtasks.size(); j++) {
                Subtask subtask = subtasks.get(j);
                System.out.print((j + 1) + ". " + subtask.getName() + " status - " + subtask.getTaskStatus() + '\n' + '\t');

            }
            System.out.println();
        }
    }

    private static void runTestForFileTaskManagerSave() {
        fileTaskManager.addTask(new Task("Убрать крвартиру.", " ", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES)));

        Epic epic1 = new Epic("Поменять пробитое колесо на велосипеде.", " ");
        fileTaskManager.addEpic(epic1);

        Subtask s1 = new Subtask("Купить новую камеру.", " ", TaskStatus.NEW, epic1.getId(), LocalDateTime.of(2025, 5, 13, 15, 15), Duration.of(20, ChronoUnit.MINUTES));
        Subtask s2 = new Subtask("Поменять колесо.", " ", TaskStatus.NEW, epic1.getId(), LocalDateTime.of(2025, 5, 13, 16, 15), Duration.of(20, ChronoUnit.MINUTES));
        fileTaskManager.addSubtask(s1);
        fileTaskManager.addSubtask(s2);
    }

    private static void runTestForFileTaskManagerRead() {
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(PATH_TO_TASK_FILE);

        System.out.println("List Task: " + fileTaskManager.getListOfTask());
        System.out.println("List Epic: " + fileTaskManager.getListOfEpics());
        System.out.println("List Subtask: " + fileTaskManager.getListOfSubtasks());

        Epic epic = fileTaskManager.getListOfEpics().get(0);
        if (epic != null)
            System.out.println("Epic subtask: " + epic.getListOfSubtasks());

        Task task = new Task(" ", " ", TaskStatus.NEW, LocalDateTime.now(), Duration.of(20, ChronoUnit.MINUTES));
        fileTaskManager.addTask(task);
    }
}
