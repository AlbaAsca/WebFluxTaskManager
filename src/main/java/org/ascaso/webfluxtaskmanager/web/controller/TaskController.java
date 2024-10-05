package org.ascaso.webfluxtaskmanager.web.controller;

import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;
import org.ascaso.webfluxtaskmanager.service.TaskService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public Mono<Task> createTask(@ModelAttribute("newTask") @Valid @RequestBody Task task) {
        return taskService.createTask(task);
    }

    @GetMapping
    public Flux<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Task>> getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Task>> updateTask(@PathVariable String id, @Valid @RequestBody Task task) {
        return taskService.updateTask(id, task)
            .map(updatedTask -> ResponseEntity.ok(updatedTask))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return taskService.deleteTask(id)
            .then(Mono.just(ResponseEntity.noContent().build()));
    }

    @GetMapping(value = "/task-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Task> streamTasks() {
        return Flux.interval(Duration.ofSeconds(10))  // Cada 10 segundos envía un nuevo evento
                .map(i -> new Task(
                        null,
                        "Tarea " + i,
                        "Descripción de la tarea " + i,
                        TaskStatus.PENDING,
                        LocalDateTime.now().plusDays(i).toLocalDate()
                ));
    }
}
