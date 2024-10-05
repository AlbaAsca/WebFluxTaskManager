package org.ascaso.webfluxtaskmanager.handler;

import org.ascaso.webfluxtaskmanager.model.Task;
import org.ascaso.webfluxtaskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class TaskHandler {

    @Autowired
    private TaskService taskService;

    public Mono<ServerResponse> getAllTasks(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(taskService.getAllTasks(), Task.class);
    }

    public Mono<ServerResponse> createTask(ServerRequest request) {
        return request.bodyToMono(Task.class)
                .flatMap(taskService::createTask)
                .flatMap(savedTask -> ServerResponse.status(201).bodyValue(savedTask));
    }
}
