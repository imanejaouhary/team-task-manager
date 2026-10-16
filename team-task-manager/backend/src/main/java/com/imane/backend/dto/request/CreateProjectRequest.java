package com.imane.backend.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProjectRequest {
    private String name;
    private String description;
    private LocalDate deadline;
    private Long teamId;
    private Long clientId;
}