package com.imane.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imane.backend.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}