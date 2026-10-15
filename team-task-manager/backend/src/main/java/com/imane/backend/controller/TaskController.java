package com.imane.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.imane.backend.dto.TaskDTO;
import com.imane.backend.dto.request.CreateTaskRequest;
import com.imane.backend.dto.request.UpdateTaskRequest;
import com.imane.backend.dto.request.UpdateTaskStatusRequest;
import com.imane.backend.security.CustomUserDetails;
import com.imane.backend.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<TaskDTO> create(@RequestBody CreateTaskRequest request,
                                          @AuthenticationPrincipal CustomUserDetails me) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request, me.getUser().getId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CLIENT')")
    public List<TaskDTO> getAll(@AuthenticationPrincipal CustomUserDetails me) {
        return taskService.getTasksFor(me.getUser().getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CLIENT')")
    public TaskDTO getById(@PathVariable Long id,
                           @AuthenticationPrincipal CustomUserDetails me) {
        return taskService.getTaskById(id, me.getUser().getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public TaskDTO update(@PathVariable Long id,
                          @RequestBody UpdateTaskRequest request,
                          @AuthenticationPrincipal CustomUserDetails me) {
        return taskService.updateTask(id, request, me.getUser().getId());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public TaskDTO updateStatus(@PathVariable Long id,
                                @RequestBody UpdateTaskStatusRequest request,
                                @AuthenticationPrincipal CustomUserDetails me) {
        return taskService.updateStatus(id, request, me.getUser().getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal CustomUserDetails me) {
        taskService.deleteTask(id, me.getUser().getId());
    }
}