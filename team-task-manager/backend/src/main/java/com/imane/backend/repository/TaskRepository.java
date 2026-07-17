package com.imane.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.imane.backend.entity.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

}