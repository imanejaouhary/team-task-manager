package com.imane.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.imane.backend.dto.ProjectDTO;
import com.imane.backend.dto.request.CreateProjectRequest;
import com.imane.backend.dto.request.UpdateProjectRequest;
import com.imane.backend.security.CustomUserDetails;
import com.imane.backend.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProjectDTO> create(@RequestBody CreateProjectRequest request,
                                             @AuthenticationPrincipal CustomUserDetails me) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(request, me.getUser().getId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CLIENT')")
    public List<ProjectDTO> getAll(@AuthenticationPrincipal CustomUserDetails me) {
        return projectService.getProjectsFor(me.getUser().getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE','CLIENT')")
    public ProjectDTO getById(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails me) {
        return projectService.getProjectById(id, me.getUser().getId());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ProjectDTO update(@PathVariable Long id,
                             @RequestBody UpdateProjectRequest request,
                             @AuthenticationPrincipal CustomUserDetails me) {
        return projectService.updateProject(id, request, me.getUser().getId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id,
                       @AuthenticationPrincipal CustomUserDetails me) {
        projectService.deleteProject(id, me.getUser().getId());
    }
}