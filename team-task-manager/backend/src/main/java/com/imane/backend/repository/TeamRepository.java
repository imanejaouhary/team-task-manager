package com.imane.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.imane.backend.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByManagerId(Long managerId);
    boolean existsByName(String name);
}