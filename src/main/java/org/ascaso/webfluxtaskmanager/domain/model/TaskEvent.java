package org.ascaso.webfluxtaskmanager.domain.model;

/** Cambio en una tarea, emitido en tiempo real por Server-Sent Events. */
public record TaskEvent(Type type, String taskId, Task task) {

    public enum Type { CREATED, UPDATED, DELETED }

    public static TaskEvent of(Type type, Task task) {
        return new TaskEvent(type, task.id(), task);
    }

    public static TaskEvent deleted(String taskId) {
        return new TaskEvent(Type.DELETED, taskId, null);
    }
}
