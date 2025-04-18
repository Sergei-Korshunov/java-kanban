package ru.korshunov.kanban;

import ru.korshunov.kanban.manager.Managers;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.util.List;

public class Main {
    private static final TaskManager taskManager = Managers.getDefaultTaskManager();

    public static void main(String[] args) {
        taskManager.addTask(new Task("Покормить кота.", "", TaskStatus.NEW));

        Epic epic1 = new Epic("Поменять пробитое колесо на велосипеде.", "");
        taskManager.addEpic(epic1);

        Subtask s1 = new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epic1.getId());
        Subtask s2 = new Subtask("Поменять колесо.", "", TaskStatus.NEW, epic1.getId());
        taskManager.addSubtask(s1);
        taskManager.addSubtask(s2);

        taskManager.getTaskOnId(1);
        taskManager.getEpicOnId(2);
        taskManager.getSubtaskOnId(3);

        System.out.println(taskManager.getHistory());

        taskManager.clearSubtasks();

        System.out.println(taskManager.getHistory());
    }

    private static void runTest() {
        // Первая малая задача
        taskManager.addTask(new Task("Убрать крвартиру.", "", TaskStatus.NEW));
        taskManager.addTask(new Task("Поиграть с кошкой.", "", TaskStatus.NEW));

        // Первая большая задача
        Epic epic1 = new Epic("Поменять пробитое колесо на велосипеде.", "");
        taskManager.addEpic(epic1);

        Subtask s1 = new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epic1.getId());
        Subtask s2 = new Subtask("Поменять колесо.", "", TaskStatus.NEW, epic1.getId());
        taskManager.addSubtask(s1);
        taskManager.addSubtask(s2);

        // Вторая большая задача
        Epic epic2 = new Epic("Сходить в магазин.", "");
        taskManager.addEpic(epic2);

        taskManager.addSubtask(new Subtask("Купить товары по списку.", "", TaskStatus.NEW, epic2.getId()));

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
}
