package org.ascaso.webfluxtaskmanager.service;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("No existe ninguna tarea con id " + id);
    }
}
