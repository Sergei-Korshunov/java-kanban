package ru.korshunov.kanban;

import ru.korshunov.kanban.manager.Managers;
import ru.korshunov.kanban.manager.TaskManager;
import ru.korshunov.kanban.manager.history.InMemoryHistoryManager;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.util.List;

public class Main {
    private static final TaskManager taskManager = Managers.getDefaultTaskManager();

    public static void main(String[] args) {
        InMemoryHistoryManager history = new InMemoryHistoryManager();
        Task task1 = new Task("1", "", TaskStatus.NEW);
        task1.setId(1);
        Task task2 = new Task("2", "", TaskStatus.NEW);
        task2.setId(2);
        Task task3 = new Task("3", "", TaskStatus.NEW);
        task3.setId(3);

        Epic task4 = new Epic("4", "");
        task4.setId(4);

        history.add(task1);
        history.add(task2);
        history.add(task3);
        history.add(task4);

        history.remove(4);
        history.remove(2);
        history.remove(1);
        history.remove(3);

//        history.add(task3);
//        history.add(task3);
//        history.add(task2);
//        history.add(task1);




//        Task task4 = new Task("2", "", TaskStatus.NEW);
//        task4.setId(2);
//        history.add(task4);
//
//        Task task5 = new Task("1", "", TaskStatus.NEW);
//        task5.setId(1);
//
//        history.add(task5);
//
//        Task task6 = new Task("2", "", TaskStatus.NEW);
//        task6.setId(2);
//
//        history.add(task6);

        System.out.println(history.getHistory());

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

        // printAllTasks();
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
