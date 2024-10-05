package org.ascaso.webfluxtaskmanager.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Manejar excepciones de recurso no encontrado (ej: Task no encontrada)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    // Manejar excepciones generales (ej: cualquier otra excepción)
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, String>>> handleGlobalException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "An unexpected error occurred");
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }

    // Manejar validaciones fallidas
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Mono<ResponseEntity<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors));
    }
}
