package com.imane.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imane.backend.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByTeamManagerId(Long managerId);

    List<Project> findByTeamId(Long teamId);

    List<Project> findByClientId(Long clientId);
}