package com.imane.backend.dto.request;

import com.imane.backend.enums.TaskPriority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private Long projectId;
    private Long assignedUserId;
}