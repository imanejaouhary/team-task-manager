package com.imane.backend.service;

import java.util.List;

import com.imane.backend.dto.ProjectDTO;
import com.imane.backend.dto.request.CreateProjectRequest;
import com.imane.backend.dto.request.UpdateProjectRequest;

public interface ProjectService {

    ProjectDTO createProject(CreateProjectRequest request, Long currentUserId);

    List<ProjectDTO> getProjectsFor(Long currentUserId);

    ProjectDTO getProjectById(Long id, Long currentUserId);

    ProjectDTO updateProject(Long id, UpdateProjectRequest request, Long currentUserId);

    void deleteProject(Long id, Long currentUserId);
}