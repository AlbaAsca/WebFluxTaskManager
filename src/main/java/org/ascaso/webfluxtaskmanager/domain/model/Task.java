package org.ascaso.webfluxtaskmanager.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    @NotEmpty(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    private TaskStatus status;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    public Task(String id, String title, String description, TaskStatus status, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
    }
}