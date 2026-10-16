package com.imane.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.imane.backend.dto.ProjectDTO;
import com.imane.backend.dto.request.CreateProjectRequest;
import com.imane.backend.dto.request.UpdateProjectRequest;
import com.imane.backend.entity.Project;
import com.imane.backend.entity.Team;
import com.imane.backend.entity.User;
import com.imane.backend.enums.ProjectStatus;
import com.imane.backend.repository.ProjectRepository;
import com.imane.backend.repository.TeamRepository;
import com.imane.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    private User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private String roleOf(User u) {
        return u.getRole().getName();
    }

    private Team loadTeam(Long teamId) {
        if (teamId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "teamId is required");
        }
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
    }

    private User loadClient(Long clientId) {
        if (clientId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clientId is required");
        }
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));
        if (!"CLIENT".equals(roleOf(client))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a CLIENT");
        }
        return client;
    }

    private Project loadProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    private boolean canManage(User me, Project p) {
        String role = roleOf(me);
        return "ADMIN".equals(role)
                || ("MANAGER".equals(role)
                    && p.getTeam().getManager().getId().equals(me.getId()));
    }

    private boolean canView(User me, Project p) {
        if (canManage(me, p)) return true;
        String role = roleOf(me);
        if ("EMPLOYEE".equals(role)) {
            return me.getTeam() != null && me.getTeam().getId().equals(p.getTeam().getId());
        }
        if ("CLIENT".equals(role)) {
            return p.getClient().getId().equals(me.getId());
        }
        return false;
    }

    private ProjectDTO toDTO(Project p) {
        return new ProjectDTO(p.getId(), p.getName(), p.getDescription(),
                p.getStatus(), p.getDeadline(),
                p.getTeam().getId(), p.getTeam().getName(),
                p.getClient().getId(),
                p.getClient().getFirstName() + " " + p.getClient().getLastName());
    }

    @Override
    public ProjectDTO createProject(CreateProjectRequest request, Long currentUserId) {
        User me = loadUser(currentUserId);

        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        Team team = loadTeam(request.getTeamId());

        if ("MANAGER".equals(roleOf(me)) && !team.getManager().getId().equals(me.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not manage this team");
        }

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setDeadline(request.getDeadline());
        project.setStatus(ProjectStatus.PLANNED);
        project.setTeam(team);
        project.setClient(loadClient(request.getClientId()));

        return toDTO(projectRepository.save(project));
    }

    @Override
    public List<ProjectDTO> getProjectsFor(Long currentUserId) {
        User me = loadUser(currentUserId);
        String role = roleOf(me);

        List<Project> projects;
        if ("ADMIN".equals(role)) {
            projects = projectRepository.findAll();
        } else if ("MANAGER".equals(role)) {
            projects = projectRepository.findByTeamManagerId(me.getId());
        } else if ("EMPLOYEE".equals(role) && me.getTeam() != null) {
            projects = projectRepository.findByTeamId(me.getTeam().getId());
        } else if ("CLIENT".equals(role)) {
            projects = projectRepository.findByClientId(me.getId());
        } else {
            projects = List.of();
        }
        return projects.stream().map(this::toDTO).toList();
    }

    @Override
    public ProjectDTO getProjectById(Long id, Long currentUserId) {
        User me = loadUser(currentUserId);
        Project p = loadProject(id);
        if (!canView(me, p)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return toDTO(p);
    }

    @Override
    public ProjectDTO updateProject(Long id, UpdateProjectRequest request, Long currentUserId) {
        User me = loadUser(currentUserId);
        Project p = loadProject(id);

        if (!canManage(me, p)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (request.getName() != null) p.setName(request.getName());
        if (request.getDescription() != null) p.setDescription(request.getDescription());
        if (request.getStatus() != null) p.setStatus(request.getStatus());
        if (request.getDeadline() != null) p.setDeadline(request.getDeadline());

        boolean changesOwnership = request.getTeamId() != null || request.getClientId() != null;
        if (changesOwnership) {
            if (!"ADMIN".equals(roleOf(me))) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Only ADMIN can change team or client");
            }
            if (request.getTeamId() != null) p.setTeam(loadTeam(request.getTeamId()));
            if (request.getClientId() != null) p.setClient(loadClient(request.getClientId()));
        }

        return toDTO(projectRepository.save(p));
    }

    @Override
    public void deleteProject(Long id, Long currentUserId) {
        User me = loadUser(currentUserId);
        Project p = loadProject(id);

        if (!canManage(me, p)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        if (!p.getTasks().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Project has tasks, delete them first");
        }
        projectRepository.delete(p);
    }
}