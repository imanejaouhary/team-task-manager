package com.imane.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.imane.backend.entity.Task;
import com.imane.backend.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @Override
    public Task updateTask(Long id, Task updatedTask) {

        Task task = getTaskById(id);

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setStatus(updatedTask.getStatus());
        task.setPriority(updatedTask.getPriority());
        task.setAssignedUser(updatedTask.getAssignedUser());
        task.setProject(updatedTask.getProject());

        return taskRepository.save(task);
    }

    @Override
    public void deleteTask(Long id) {

        Task task = getTaskById(id);

        taskRepository.delete(task);
    }

    @Override
    public List<Task> getTasksByTeamId(Long teamId) {
        return taskRepository.findByProjectTeamId(teamId);
    }

    @Override
    public List<Task> getTasksByAssignedUserId(Long userId) {
        return taskRepository.findByAssignedUserId(userId);
    }
}