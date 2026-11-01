package org.ascaso.webfluxtaskmanager.web;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

import org.ascaso.webfluxtaskmanager.domain.model.Task;
import org.ascaso.webfluxtaskmanager.domain.model.TaskStatus;
import org.ascaso.webfluxtaskmanager.service.TaskService;
import org.ascaso.webfluxtaskmanager.web.dto.TaskRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/** API REST de tareas con enlaces HATEOAS (HAL): el cliente navega por los links, no construye URLs. */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tareas")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Lista las tareas por fecha límite; filtro opcional por estado")
    public Mono<CollectionModel<EntityModel<Task>>> findAll(@RequestParam(required = false) TaskStatus status) {
        return service.findAll(status)
                .concatMap(this::toModel)
                .collectList()
                .zipWith(linkTo(methodOn(TaskController.class).findAll(status)).withSelfRel().toMono(),
                        CollectionModel::of);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de una tarea")
    public Mono<EntityModel<Task>> findById(@PathVariable String id) {
        return service.findById(id).flatMap(this::toModel);
    }

    @PostMapping
    @Operation(summary = "Crea una tarea")
    public Mono<ResponseEntity<EntityModel<Task>>> create(@Valid @RequestBody TaskRequest request) {
        return service.create(request)
                .flatMap(this::toModel)
                .map(model -> ResponseEntity.created(model.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(model));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifica una tarea")
    public Mono<EntityModel<Task>> update(@PathVariable String id, @Valid @RequestBody TaskRequest request) {
        return service.update(id, request).flatMap(this::toModel);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borra una tarea (solo ADMIN)")
    public Mono<ResponseEntity<Void>> delete(@PathVariable String id) {
        return service.delete(id).then(Mono.just(ResponseEntity.noContent().build()));
    }

    private Mono<EntityModel<Task>> toModel(Task task) {
        TaskController self = methodOn(TaskController.class);
        return Flux.concat(
                        linkTo(self.findById(task.id())).withSelfRel().toMono(),
                        linkTo(self.update(task.id(), null)).withRel("update").toMono(),
                        linkTo(self.delete(task.id())).withRel("delete").toMono(),
                        linkTo(self.findAll(null)).withRel("tasks").toMono())
                .collectList()
                .map(links -> EntityModel.of(task, links));
    }
}
