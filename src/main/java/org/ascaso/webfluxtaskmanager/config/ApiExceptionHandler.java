package org.ascaso.webfluxtaskmanager.config;

import java.util.Map;
import java.util.stream.Collectors;

import org.ascaso.webfluxtaskmanager.service.TaskNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/** Errores en formato estándar RFC 9457 (application/problem+json). */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    ProblemDetail notFound(TaskNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Tarea no encontrada");
        return problem;
    }

    @ExceptionHandler(WebExchangeBindException.class)
    ProblemDetail invalid(WebExchangeBindException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "La tarea no es válida");
        problem.setTitle("Datos no válidos");
        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, e -> String.valueOf(e.getDefaultMessage()), (a, b) -> a));
        problem.setProperty("errors", errors);
        return problem;
    }
}
