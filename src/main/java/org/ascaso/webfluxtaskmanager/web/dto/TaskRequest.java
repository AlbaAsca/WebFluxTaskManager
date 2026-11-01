package org.ascaso.webfluxtaskmanager.web.dto;

import java.time.LocalDate;

import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Cuerpo de alta y modificación: el cliente nunca envía id ni fechas de auditoría. */
public record TaskRequest(
        @NotBlank(message = "El título es obligatorio") @Size(max = 120) String title,
        @Size(max = 2000) String description,
        @NotNull(message = "El estado es obligatorio") TaskStatus status,
        @NotNull(message = "La fecha límite es obligatoria") LocalDate dueDate) {
}
