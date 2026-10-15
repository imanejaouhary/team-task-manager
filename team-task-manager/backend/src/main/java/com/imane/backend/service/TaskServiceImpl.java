package com.imane.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.imane.backend.dto.TaskDTO;
import com.imane.backend.dto.request.CreateTaskRequest;
import com.imane.backend.dto.request.UpdateTaskRequest;
import com.imane.backend.dto.request.UpdateTaskStatusRequest;
import com.imane.backend.entity.Project;
import com.imane.backend.entity.Task;
import com.imane.backend.entity.User;
import com.imane.backend.enums.TaskPriority;
import com.imane.backend.enums.TaskStatus;
import com.imane.backend.repository.ProjectRepository;
import com.imane.backend.repository.TaskRepository;
import com.imane.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    private User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private String roleOf(User u) {
        return u.getRole().getName();
    }

    private Task loadTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    private Project loadProject(Long id) {
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "projectId is required");
        }
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    private boolean managesProject(User me, Project p) {
        String role = roleOf(me);
        return "ADMIN".equals(role)
                || ("MANAGER".equals(role)
                    && p.getTeam().getManager().getId().equals(me.getId()));
    }

    private boolean isAssignedTo(User me, Task t) {
        return t.getAssignedUser() != null
                && t.getAssignedUser().getId().equals(me.getId());
    }

    private boolean canView(User me, Task t) {
        if (managesProject(me, t.getProject())) return true;
        String role = roleOf(me);
        if ("EMPLOYEE".equals(role)) return isAssignedTo(me, t);
        if ("CLIENT".equals(role)) {
            return t.getProject().getClient().getId().equals(me.getId());
        }
        return false;
    }

    private User loadAssignee(Long userId, Project project) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Assignee not found"));
        if (!"EMPLOYEE".equals(roleOf(u))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee must be an EMPLOYEE");
        }
        if (u.getTeam() == null || !u.getTeam().getId().equals(project.getTeam().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Assignee must belong to the project's team");
        }
        return u;
    }

    private TaskDTO toDTO(Task t) {
        User a = t.getAssignedUser();
        return new TaskDTO(t.getId(), t.getTitle(), t.getDescription(),
                t.getStatus(), t.getPriority(),
                t.getProject().getId(), t.getProject().getName(),
                a == null ? null : a.getId(),
                a == null ? null : a.getFirstName() + " " + a.getLastName());
    }

    @Override
    public TaskDTO createTask(CreateTaskRequest request, Long currentUserId) {
        User me = loadUser(currentUserId);
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title is required");
        }
        Project project = loadProject(request.getProjectId());
        if (!managesProject(me, project)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setStatus(TaskStatus.TODO);
        task.setProject(project);
        if (request.getAssignedUserId() != null) {
            task.setAssignedUser(loadAssignee(request.getAssignedUserId(), project));
        }
        return toDTO(taskRepository.save(task));
    }

    @Override
    public List<TaskDTO> getTasksFor(Long currentUserId) {
        User me = loadUser(currentUserId);
        String role = roleOf(me);

        List<Task> tasks;
        if ("ADMIN".equals(role)) {
            tasks = taskRepository.findAll();
        } else if ("MANAGER".equals(role)) {
            tasks = taskRepository.findByProjectTeamManagerId(me.getId());
        } else if ("EMPLOYEE".equals(role)) {
            tasks = taskRepository.findByAssignedUserId(me.getId());
        } else if ("CLIENT".equals(role)) {
            tasks = taskRepository.findByProjectClientId(me.getId());
        } else {
            tasks = List.of();
        }
        return tasks.stream().map(this::toDTO).toList();
    }

    @Override
    public TaskDTO getTaskById(Long id, Long currentUserId) {
        User me = loadUser(currentUserId);
        Task t = loadTask(id);
        if (!canView(me, t)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return toDTO(t);
    }

    @Override
    public TaskDTO updateTask(Long id, UpdateTaskRequest request, Long currentUserId) {
        User me = loadUser(currentUserId);
        Task t = loadTask(id);
        if (!managesProject(me, t.getProject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        if (request.getTitle() != null) t.setTitle(request.getTitle());
        if (request.getDescription() != null) t.setDescription(request.getDescription());
        if (request.getStatus() != null) t.setStatus(request.getStatus());
        if (request.getPriority() != null) t.setPriority(request.getPriority());
        if (request.getAssignedUserId() != null) {
            t.setAssignedUser(loadAssignee(request.getAssignedUserId(), t.getProject()));
        }
        return toDTO(taskRepository.save(t));
    }

    @Override
    public TaskDTO updateStatus(Long id, UpdateTaskStatusRequest request, Long currentUserId) {
        User me = loadUser(currentUserId);
        Task t = loadTask(id);
        boolean allowed = managesProject(me, t.getProject())
                || ("EMPLOYEE".equals(roleOf(me)) && isAssignedTo(me, t));
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        if (request.getStatus() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }
        t.setStatus(request.getStatus());
        return toDTO(taskRepository.save(t));
    }

    @Override
    public void deleteTask(Long id, Long currentUserId) {
        User me = loadUser(currentUserId);
        Task t = loadTask(id);
        if (!managesProject(me, t.getProject())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        taskRepository.delete(t);
    }
}