package com.imane.backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeamRequest {
    private String name;
    private String description;
    private Long managerId;
}