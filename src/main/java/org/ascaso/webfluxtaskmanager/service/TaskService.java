package org.ascaso.webfluxtaskmanager.service;

import org.ascaso.webfluxtaskmanager.exception.ResourceNotFoundException;
import org.ascaso.webfluxtaskmanager.model.Task;
import org.ascaso.webfluxtaskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Mono<Task> createTask(Task task) {
        return taskRepository.save(task);
    }

    public Flux<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Mono<Task> getTaskById(String id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Task not found with id: " + id)));
    }

    public Mono<Task> updateTask(String id, Task task) {
        return taskRepository.findById(id)
            .flatMap(existingTask -> {
                existingTask.setTitle(task.getTitle());
                existingTask.setDescription(task.getDescription());
                existingTask.setStatus(task.getStatus());
                existingTask.setDueDate(task.getDueDate());
                return taskRepository.save(existingTask);
            });
    }

    public Mono<Void> deleteTask(String id) {
        return taskRepository.deleteById(id);
    }
}
