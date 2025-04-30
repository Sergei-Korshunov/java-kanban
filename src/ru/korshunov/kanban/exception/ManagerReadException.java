package ru.korshunov.kanban.exception;

public class ManagerReadException extends  RuntimeException {

    public ManagerReadException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
