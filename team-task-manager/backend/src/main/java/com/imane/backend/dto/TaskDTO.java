package com.imane.backend.dto;

import com.imane.backend.enums.TaskPriority;
import com.imane.backend.enums.TaskStatus;

public record TaskDTO(Long id, String title, String description,
                      TaskStatus status, TaskPriority priority,
                      Long projectId, String projectName,
                      Long assignedUserId, String assignedUserName) {}