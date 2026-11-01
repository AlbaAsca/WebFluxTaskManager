package org.ascaso.webfluxtaskmanager.web;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.time.Duration;

import org.ascaso.webfluxtaskmanager.domain.model.TaskEvent;
import org.ascaso.webfluxtaskmanager.service.TaskService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Flux;

/**
 * Estilo funcional de WebFlux (frente al {@code @RestController}): un flujo SSE con cada alta,
 * cambio o borrado de tareas. Un latido cada 15 s mantiene viva la conexión tras proxies.
 */
@Configuration
public class TaskEventsRouter {

    public static final String PATH = "/api/tasks/events";

    @Bean
    RouterFunction<ServerResponse> taskEventsRoute(TaskService service) {
        return route(GET(PATH), request -> {
            Flux<ServerSentEvent<TaskEvent>> changes = service.events()
                    .map(event -> ServerSentEvent.builder(event).event(event.type().name()).id(event.taskId()).build());
            // El primer latido sale al instante: así el cliente recibe las cabeceras sin esperar al primer cambio.
            Flux<ServerSentEvent<TaskEvent>> heartbeat = Flux.interval(Duration.ZERO, Duration.ofSeconds(15))
                    .map(tick -> ServerSentEvent.<TaskEvent>builder().comment("heartbeat").build());
            return ServerResponse.ok()
                    .contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(Flux.merge(changes, heartbeat), new ParameterizedTypeReference<ServerSentEvent<TaskEvent>>() { });
        });
    }
}
