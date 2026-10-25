package com.imane.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRoleRequest {
    private String roleName;
    private Long teamId;
    private boolean unassignOpenTasks;
}