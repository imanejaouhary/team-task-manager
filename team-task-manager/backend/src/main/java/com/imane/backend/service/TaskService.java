package com.imane.backend.service;

import java.util.List;

import com.imane.backend.dto.TaskDTO;
import com.imane.backend.dto.request.CreateTaskRequest;
import com.imane.backend.dto.request.UpdateTaskRequest;
import com.imane.backend.dto.request.UpdateTaskStatusRequest;

public interface TaskService {

    TaskDTO createTask(CreateTaskRequest request, Long currentUserId);

    List<TaskDTO> getTasksFor(Long currentUserId);

    TaskDTO getTaskById(Long id, Long currentUserId);

    TaskDTO updateTask(Long id, UpdateTaskRequest request, Long currentUserId);

    TaskDTO updateStatus(Long id, UpdateTaskStatusRequest request, Long currentUserId);

    void deleteTask(Long id, Long currentUserId);
}