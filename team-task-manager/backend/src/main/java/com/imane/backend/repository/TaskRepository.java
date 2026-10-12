package com.imane.backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.imane.backend.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectTeamId(Long teamId);
    List<Task> findByAssignedUserId(Long userId);
    List<Task> findByProjectId(Long projectId);
}