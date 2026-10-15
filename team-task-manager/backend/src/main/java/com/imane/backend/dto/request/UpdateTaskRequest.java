package com.imane.backend.dto.request;

import com.imane.backend.enums.TaskPriority;
import com.imane.backend.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long assignedUserId;
}