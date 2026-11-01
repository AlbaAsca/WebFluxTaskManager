package org.ascaso.webfluxtaskmanager.domain.model;

import java.time.Instant;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.hateoas.server.core.Relation;

/** Tarea persistida en MongoDB. Inmutable: los cambios crean una copia con {@code withContent}. */
@Document(collection = "tasks")
@Relation(itemRelation = "task", collectionRelation = "tasks")
public record Task(
        @Id String id,
        String title,
        String description,
        TaskStatus status,
        LocalDate dueDate,
        @CreatedDate Instant createdAt,
        @LastModifiedDate Instant updatedAt) {

    public static Task newTask(String title, String description, TaskStatus status, LocalDate dueDate) {
        return new Task(null, title, description, status, dueDate, null, null);
    }

    public Task withContent(String title, String description, TaskStatus status, LocalDate dueDate) {
        return new Task(id, title, description, status, dueDate, createdAt, updatedAt);
    }
}
