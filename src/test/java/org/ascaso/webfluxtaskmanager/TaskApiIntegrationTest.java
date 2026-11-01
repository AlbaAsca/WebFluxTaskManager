package org.ascaso.webfluxtaskmanager;

import java.time.Duration;
import java.util.Map;

import org.ascaso.webfluxtaskmanager.domain.model.TaskEvent;
import org.ascaso.webfluxtaskmanager.web.TaskEventsRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/** De punta a punta contra un MongoDB real en Docker (Testcontainers). Se salta si no hay Docker. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers(disabledWithoutDocker = true)
class TaskApiIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongo = new MongoDBContainer("mongo:8.0");

    @Autowired
    private WebTestClient client;

    @Test
    void fullLifecycleIsPersistedAndStreamed() {
        WebTestClient user = client.mutate().defaultHeaders(h -> h.setBasicAuth("user", "user")).build();
        WebTestClient admin = client.mutate().defaultHeaders(h -> h.setBasicAuth("admin", "admin")).build();

        Flux<ServerSentEvent<TaskEvent>> events = user.get().uri(TaskEventsRouter.PATH)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<ServerSentEvent<TaskEvent>>() { })
                .getResponseBody()
                .filter(sse -> sse.data() != null);

        StepVerifier.create(events.map(sse -> sse.data().type()).take(3))
                .then(() -> {
                    String location = user.post().uri("/api/tasks")
                            .bodyValue(Map.of("title", "Estudiar S3", "status", "PENDING", "dueDate", "2026-11-25"))
                            .exchange()
                            .expectStatus().isCreated()
                            .returnResult(String.class).getResponseHeaders().getLocation().getPath();

                    user.put().uri(location)
                            .bodyValue(Map.of("title", "Estudiar S3", "status", "COMPLETED", "dueDate", "2026-11-25"))
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody().jsonPath("$.status").isEqualTo("COMPLETED")
                            .jsonPath("$.createdAt").exists();

                    user.get().uri("/api/tasks?status=COMPLETED").exchange()
                            .expectStatus().isOk()
                            .expectBody().jsonPath("$._embedded.tasks[0].title").isEqualTo("Estudiar S3");

                    admin.delete().uri(location).exchange().expectStatus().isNoContent();
                    user.get().uri(location).exchange().expectStatus().isNotFound();
                })
                .expectNext(TaskEvent.Type.CREATED, TaskEvent.Type.UPDATED, TaskEvent.Type.DELETED)
                .expectComplete()
                .verify(Duration.ofSeconds(20));
    }
}
