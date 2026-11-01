package org.ascaso.webfluxtaskmanager.service;

import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.ascaso.webfluxtaskmanager.domain.model.TaskEvent;
import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;
import org.ascaso.webfluxtaskmanager.domain.repository.TaskRepository;
import org.ascaso.webfluxtaskmanager.web.dto.TaskRequest;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
public class TaskService {

    private final TaskRepository repository;
    // Multicast sin histórico: cada cliente SSE recibe los cambios desde que se conecta.
    private final Sinks.Many<TaskEvent> events = Sinks.many().multicast().directBestEffort();

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public Flux<Task> findAll(TaskStatus status) {
        return status == null
                ? repository.findAllByOrderByDueDateAsc()
                : repository.findByStatusOrderByDueDateAsc(status);
    }

    public Mono<Task> findById(String id) {
        return repository.findById(id).switchIfEmpty(Mono.error(() -> new TaskNotFoundException(id)));
    }

    public Mono<Task> create(TaskRequest request) {
        return repository.save(Task.newTask(request.title(), request.description(), request.status(), request.dueDate()))
                .doOnNext(task -> emit(TaskEvent.of(TaskEvent.Type.CREATED, task)));
    }

    public Mono<Task> update(String id, TaskRequest request) {
        return findById(id)
                .map(task -> task.withContent(request.title(), request.description(), request.status(), request.dueDate()))
                .flatMap(repository::save)
                .doOnNext(task -> emit(TaskEvent.of(TaskEvent.Type.UPDATED, task)));
    }

    public Mono<Void> delete(String id) {
        return findById(id)
                .flatMap(repository::delete)
                .doOnSuccess(ignored -> emit(TaskEvent.deleted(id)));
    }

    public Flux<TaskEvent> events() {
        return events.asFlux();
    }

    private void emit(TaskEvent event) {
        events.tryEmitNext(event);
    }
}
