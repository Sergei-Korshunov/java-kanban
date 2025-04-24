package ru.korshunov.kanban.manager;

import ru.korshunov.kanban.exception.ManagerReadException;
import ru.korshunov.kanban.exception.ManagerSaveException;
import ru.korshunov.kanban.task.Epic;
import ru.korshunov.kanban.task.Subtask;
import ru.korshunov.kanban.task.Task;
import ru.korshunov.kanban.task.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTaskManager(Path pathToFile) {
        this.path = pathToFile;
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        FileBackedTaskManager fileTaskManager = new FileBackedTaskManager(path);

        try {
            List<String> list = Files.readAllLines(path);
            int lastTaskId = 0;

            for (int i = 1; i < list.size(); i++) {
                Task task = fileTaskManager.fromString(list.get(i));
                int id = task.getId();

                if (task instanceof Epic) {
                    fileTaskManager.epics.put(id, (Epic) task);
                } else if (task instanceof Subtask) {
                    fileTaskManager.subtasks.put(id, (Subtask) task);

                    fileTaskManager.fillEpicWithSubtasks((Subtask) task);
                } else {
                    fileTaskManager.tasks.put(id, task);
                }

                if (lastTaskId < id) {
                    lastTaskId = id;
                }
            }

            fileTaskManager.setTaskId(lastTaskId);
        } catch (IOException e) {
            throw new ManagerReadException("Ошибка чтения данных из файла.", e);
        }

        return fileTaskManager;
    }

    private void fillEpicWithSubtasks(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask);
        }
    }

    protected Task fromString(String value) {
        String[] objectData = value.split(",");

        int id = Integer.parseInt(objectData[0]);
        TaskType typeTask = TaskType.valueOf(objectData[1]);
        String name = objectData[2];
        TaskStatus taskStatus = TaskStatus.valueOf(objectData[3]);
        String description = objectData[4];
        int epicId = objectData.length == 6 ? Integer.parseInt(objectData[5]) : 0;

        switch (typeTask) {
            case TASK:
                return Task.getInstance(id, name, taskStatus, description);
            case EPIC:
                return Epic.getInstance(id, name, taskStatus, description);
            case SUBTASK:
                return Subtask.getInstance(id, name, taskStatus, description, epicId);
            default:
                return null;
        }
    }

    protected void save() {
        if (Files.notExists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String head = "id,type,name,status,description,epic\n";

        try (FileWriter fileWriter = new FileWriter(path.toFile(), false)) {
            fileWriter.write(head);

            for (Task task : getListOfTask()) {
                fileWriter.write(toString(task));
            }

            for (Epic epic : getListOfEpics()) {
                fileWriter.write(toString(epic));
            }

            for (Subtask subtask : getListOfSubtasks()) {
                fileWriter.write(toString(subtask));
            }
        } catch (IOException e) {
                throw new ManagerSaveException("Ошибка сохранения данных в файл.", e);
        }
    }

    protected String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        TaskType typeTask = TaskType.TASK;
        int epicId = -1;

        if (task instanceof Epic) {
            typeTask = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            typeTask = TaskType.SUBTASK;
            epicId = ((Subtask) task).getEpicId();
        }

        sb.append(task.getId()).append(",");
        sb.append(typeTask).append(",");
        sb.append(task.getName()).append(",");
        sb.append(task.getTaskStatus()).append(",");
        sb.append(task.getDescription());

        if (epicId != -1) {
            sb.append(",");
            sb.append(epicId);
        }
        sb.append('\n');

        return sb.toString();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }
}
