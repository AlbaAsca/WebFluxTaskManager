package org.ascaso.webfluxtaskmanager.domain.repository;

import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    // Puedes añadir métodos personalizados aquí si lo necesitas
}
