package com.imane.backend.dto.request;

import com.imane.backend.enums.TaskStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskStatusRequest {
    private TaskStatus status;
}