package org.ascaso.webfluxtaskmanager.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.LocalDate;

import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.ascaso.webfluxtaskmanager.domain.model.TaskEvent;
import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;
import org.ascaso.webfluxtaskmanager.domain.repository.TaskRepository;
import org.ascaso.webfluxtaskmanager.web.dto.TaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class TaskServiceTest {

    private static final TaskRequest REQUEST =
            new TaskRequest("Repasar IAM", null, TaskStatus.PENDING, LocalDate.of(2026, 11, 20));

    private TaskRepository repository;
    private TaskService service;

    @BeforeEach
    void setUp() {
        repository = mock(TaskRepository.class);
        service = new TaskService(repository);
    }

    @Test
    void createSavesTheTaskAndPublishesAnEvent() {
        Task saved = new Task("1", "Repasar IAM", null, TaskStatus.PENDING, REQUEST.dueDate(), null, null);
        given(repository.save(any())).willReturn(Mono.just(saved));

        StepVerifier.create(service.events().take(1))
                .then(() -> service.create(REQUEST).subscribe())
                .expectNextMatches(event -> event.type() == TaskEvent.Type.CREATED && event.taskId().equals("1"))
                .expectComplete()
                .verify(Duration.ofSeconds(2));
    }

    @Test
    void updatingAMissingTaskFailsWithNotFound() {
        given(repository.findById("x")).willReturn(Mono.empty());

        StepVerifier.create(service.update("x", REQUEST)).expectError(TaskNotFoundException.class).verify();
        verify(repository, never()).save(any());
    }

    @Test
    void updateKeepsIdAndAuditDates() {
        Task existing = new Task("7", "Viejo", "d", TaskStatus.PENDING, LocalDate.of(2026, 1, 1), null, null);
        given(repository.findById("7")).willReturn(Mono.just(existing));
        given(repository.save(any())).willAnswer(call -> Mono.just(call.getArgument(0)));

        StepVerifier.create(service.update("7", REQUEST))
                .expectNextMatches(task -> task.id().equals("7") && task.title().equals("Repasar IAM"))
                .verifyComplete();
    }

    @Test
    void deletingPublishesADeletedEvent() {
        Task existing = new Task("9", "t", null, TaskStatus.COMPLETED, LocalDate.now(), null, null);
        given(repository.findById("9")).willReturn(Mono.just(existing));
        given(repository.delete(existing)).willReturn(Mono.empty());

        StepVerifier.create(service.events().take(1))
                .then(() -> service.delete("9").subscribe())
                .expectNextMatches(event -> event.type() == TaskEvent.Type.DELETED && event.taskId().equals("9"))
                .expectComplete()
                .verify(Duration.ofSeconds(2));
    }
}
