package org.ascaso.webfluxtaskmanager.repository;

import org.ascaso.webfluxtaskmanager.model.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    // Puedes añadir métodos personalizados aquí si lo necesitas
}
