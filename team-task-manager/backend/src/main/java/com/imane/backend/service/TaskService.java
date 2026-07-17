package com.imane.backend.service;

import java.util.List;

import com.imane.backend.entity.Task;

public interface TaskService {

    Task createTask(Task task);

    List<Task> getAllTasks();

    Task getTaskById(Long id);

    Task updateTask(Long id, Task task);

    void deleteTask(Long id);

}