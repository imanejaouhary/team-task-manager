package com.imane.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imane.backend.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTeamId(Long teamId);
    List<Task> findByAssignedUserId(Long userId);

}