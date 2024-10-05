package org.ascaso.webfluxtaskmanager;

import org.ascaso.webfluxtaskmanager.config.security.SecurityConfig;
import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")  // Perfil de prueba para desactivar la seguridad
public class TaskControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGetAllTasks() {
        // Realizar la solicitud GET y verificar la respuesta
        webTestClient.get().uri("/tasks")
                .exchange()  // Realiza la solicitud
                .expectStatus().isOk()
                .expectBodyList(Task.class)
                .hasSize(0); // Asumimos que la base de datos está vacía inicialmente
    }

    @Test
    public void testCreateTask() {
        Task task = new Task(null, "New Task", "New Description", TaskStatus.PENDING, LocalDate.now());

        // Enviar la solicitud POST y verificar la respuesta
        webTestClient.post().uri("/tasks")
                .bodyValue(task)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Task.class)
                .value(responseTask -> {
                    // Verificar que la tarea fue creada correctamente
                    assert responseTask.getId() != null;
                    assert responseTask.getTitle().equals(task.getTitle());
                });
    }
}
