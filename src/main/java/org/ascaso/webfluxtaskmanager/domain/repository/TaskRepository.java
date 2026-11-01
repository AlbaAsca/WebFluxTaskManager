package org.ascaso.webfluxtaskmanager.domain.repository;

import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {

    Flux<Task> findByStatusOrderByDueDateAsc(TaskStatus status);

    Flux<Task> findAllByOrderByDueDateAsc();
}
