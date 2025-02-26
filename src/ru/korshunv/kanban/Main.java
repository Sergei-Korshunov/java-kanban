package ru.korshunv.kanban;

import java.util.List;

public class Main {
    private static final TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {

    }

    private static void runTest() {
        // Первая малая задача
        taskManager.addTask(new Task("Убрать крвартиру.", TaskStatus.NEW));
        taskManager.addTask(new Task("Поиграть с кошкой.", TaskStatus.NEW));

        // Первая большая задача
        Epic epic1 = new Epic("Поменять пробитое колесо на велосипеде.", TaskStatus.NEW);
        epic1.addSubtask(new Subtask("Купить новую камеру.", TaskStatus.NEW));
        epic1.addSubtask(new Subtask("Поменять колесо.", TaskStatus.NEW));

        taskManager.addEpic(epic1);

        // Вторая большая задача
        Epic epic2 = new Epic("Сходить в магазин.", TaskStatus.NEW);
        epic2.addSubtask(new Subtask("Купить товары по списку.", TaskStatus.NEW));

        taskManager.addEpic(epic2);

        printAllTasks();

        // Обновляем данные
        taskManager.removeTask(1);
        taskManager.updateTask(2, new Task("Выгулить собаку.", TaskStatus.NEW));

        Epic epic = taskManager.getEpicOnId(1);
        Subtask subtask1 = epic.getSubtaskOnId(1);
        subtask1.setTaskStatus(TaskStatus.IN_PROGRESS);

        Subtask subtask2 = epic.getSubtaskOnId(2);
        subtask2.setTaskStatus(TaskStatus.DONE);
        epic.updateSubtask(1, subtask1);
        epic.updateSubtask(2, subtask2);

        printAllTasks();

        // Обновляем данные
        subtask1.setTaskStatus(TaskStatus.DONE);
        epic.updateSubtask(1, subtask1);

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
