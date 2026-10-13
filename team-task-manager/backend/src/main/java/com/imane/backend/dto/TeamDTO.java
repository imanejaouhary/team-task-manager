package com.imane.backend.dto;

public record TeamDTO(Long id, String name, String description,
                      Long managerId, String managerName) {}