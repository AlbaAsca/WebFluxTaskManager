package org.ascaso.webfluxtaskmanager;

import org.ascaso.webfluxtaskmanager.web.handler.TaskHandler;
import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;
import org.ascaso.webfluxtaskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

public class TaskControllerTest {

    private TaskService taskService;
    private TaskHandler handler;
    private WebTestClient webTestClient;

    @BeforeEach
    void setup() {
        taskService = Mockito.mock(TaskService.class);
        webTestClient = WebTestClient.bindToRouterFunction(
                route(GET("/tasks"), handler::getAllTasks)
                        .andRoute(POST("/tasks"), handler::createTask)
        ).build();
    }

    @Test
    public void testGetAllTasks() {
        Task task = new Task("1", "Test Task", "Description", TaskStatus.IN_PROGRESS, LocalDate.now());
        Flux<Task> taskFlux = Flux.just(task);

        given(taskService.getAllTasks()).willReturn(taskFlux);

        webTestClient.get().uri("/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(1)
                .contains(task);
    }

    @Test
    public void testCreateTask() {
        Task task = new Task(null, "New Task", "New Description", TaskStatus.PENDING, LocalDate.now());
        Task createdTask = new Task("1", "New Task", "New Description", TaskStatus.PENDING, LocalDate.now());

        given(taskService.createTask(any(Task.class))).willReturn(Mono.just(createdTask));

        webTestClient.post().uri("/tasks")
                .bodyValue(task)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Task.class)
                .isEqualTo(createdTask);
    }
}
