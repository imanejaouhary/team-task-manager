package com.imane.backend.dto.request;

import java.time.LocalDate;
import com.imane.backend.enums.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProjectRequest {
    private String name;
    private String description;
    private ProjectStatus status;
    private LocalDate deadline;
    private Long teamId;     // ADMIN seulement
    private Long clientId;   // ADMIN seulement
}