package org.ascaso.webfluxtaskmanager.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

import java.time.LocalDate;
import java.util.Map;

import org.ascaso.webfluxtaskmanager.config.ApiExceptionHandler;
import org.ascaso.webfluxtaskmanager.config.HateoasConfig;
import org.ascaso.webfluxtaskmanager.config.SecurityConfig;
import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;
import org.ascaso.webfluxtaskmanager.service.TaskNotFoundException;
import org.ascaso.webfluxtaskmanager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(TaskController.class)
@Import({ SecurityConfig.class, HateoasConfig.class, ApiExceptionHandler.class })
class TaskControllerTest {

    private static final Task TASK = new Task("42", "Preparar el examen", "AWS Cloud Practitioner",
            TaskStatus.IN_PROGRESS, LocalDate.of(2026, 11, 30), null, null);

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private TaskService service;

    @Test
    void rejectsAnonymousRequests() {
        client.get().uri("/api/tasks").exchange().expectStatus().isUnauthorized();
    }

    @Test
    void listsTasksWithHypermediaLinks() {
        given(service.findAll(null)).willReturn(Flux.just(TASK));

        client.mutateWith(mockUser().roles("USER")).get().uri("/api/tasks").exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$._embedded.tasks[0].title").isEqualTo("Preparar el examen")
                .jsonPath("$._embedded.tasks[0]._links.self.href").value(href -> href.toString().endsWith("/api/tasks/42"))
                .jsonPath("$._embedded.tasks[0]._links.update.href").exists()
                .jsonPath("$._embedded.tasks[0]._links.delete.href").exists()
                .jsonPath("$._links.self.href").exists();
    }

    @Test
    void filtersByStatus() {
        given(service.findAll(TaskStatus.COMPLETED)).willReturn(Flux.empty());

        client.mutateWith(mockUser().roles("USER")).get().uri("/api/tasks?status=COMPLETED").exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$._links.self.href").value(href -> href.toString().contains("status=COMPLETED"));
    }

    @Test
    void returnsProblemDetailWhenTaskDoesNotExist() {
        given(service.findById("nope")).willReturn(Mono.error(new TaskNotFoundException("nope")));

        client.mutateWith(mockUser().roles("USER")).get().uri("/api/tasks/nope").exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Tarea no encontrada")
                .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void validatesTheRequestBody() {
        client.mutateWith(mockUser().roles("USER")).post().uri("/api/tasks")
                .bodyValue(Map.of("description", "sin título ni estado"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errors.title").isEqualTo("El título es obligatorio")
                .jsonPath("$.errors.status").exists()
                .jsonPath("$.errors.dueDate").exists();
    }

    @Test
    void createsATaskAndReturnsItsLocation() {
        given(service.create(any())).willReturn(Mono.just(TASK));

        client.mutateWith(mockUser().roles("USER")).post().uri("/api/tasks")
                .bodyValue(Map.of("title", "Preparar el examen", "status", "IN_PROGRESS", "dueDate", "2026-11-30"))
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().value("Location", location -> location.endsWith("/api/tasks/42"));
    }

    @Test
    void onlyAdminsCanDelete() {
        given(service.delete(eq("42"))).willReturn(Mono.empty());

        client.mutateWith(mockUser().roles("USER")).delete().uri("/api/tasks/42").exchange()
                .expectStatus().isForbidden();
        client.mutateWith(mockUser().roles("USER", "ADMIN")).delete().uri("/api/tasks/42").exchange()
                .expectStatus().isNoContent();
    }
}
