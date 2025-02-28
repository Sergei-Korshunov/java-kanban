package ru.korshunv.kanban;

import java.util.List;

public class Main {
    private static final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        runTest();
    }

    private static void runTest() {
        // Первая малая задача
        taskManager.addTask(new Task("Убрать крвартиру.", "", TaskStatus.NEW));
        taskManager.addTask(new Task("Поиграть с кошкой.", "", TaskStatus.NEW));

        // Первая большая задача
        // Change a tire on a bike.
        Epic epic1 = new Epic("Поменять пробитое колесо на велосипеде.", "", TaskStatus.NEW);
        taskManager.addEpic(epic1);

        taskManager.addSubtask(new Subtask("Купить новую камеру.", "", TaskStatus.NEW, epic1.getId()));
        taskManager.addSubtask(new Subtask("Поменять колесо.", "", TaskStatus.NEW, epic1.getId()));

        // Вторая большая задача
        Epic epic2 = new Epic("Сходить в магазин.", "", TaskStatus.NEW);
        taskManager.addEpic(epic2);

        taskManager.addSubtask(new Subtask("Купить товары по списку.", "", TaskStatus.NEW, epic2.getId()));

        printAllTasks();

        // Обновляем данные
        taskManager.removeTask(1);

        Subtask subtask1 = taskManager.getSubtaskOnId(4);
        subtask1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);

        Subtask subtask2 = taskManager.getSubtaskOnId(5);
        subtask2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask2);

        printAllTasks();

        // Обновляем данные
        subtask2.setTaskStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask2);

        taskManager.removeSubtask(subtask1.getId());

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
