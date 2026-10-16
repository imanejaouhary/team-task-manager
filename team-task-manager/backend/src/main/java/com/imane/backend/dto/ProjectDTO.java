package com.imane.backend.dto;

import java.time.LocalDate;
import com.imane.backend.enums.ProjectStatus;

public record ProjectDTO(Long id, String name, String description,
                         ProjectStatus status, LocalDate deadline,
                         Long teamId, String teamName,
                         Long clientId, String clientName) {}