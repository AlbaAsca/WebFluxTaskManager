package org.ascaso.webfluxtaskmanager.web.router;

import org.ascaso.webfluxtaskmanager.web.handler.TaskHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TaskRouter {

    @Bean
    public RouterFunction<ServerResponse> taskRoutes(TaskHandler handler) {
        return route(GET("/tasks"), handler::getAllTasks)
                .andRoute(POST("/tasks"), handler::createTask);
    }
}
