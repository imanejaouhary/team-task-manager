package com.imane.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.imane.backend.dto.TeamDTO;
import com.imane.backend.dto.request.CreateTeamRequest;
import com.imane.backend.dto.request.UpdateTeamRequest;
import com.imane.backend.entity.Team;
import com.imane.backend.entity.User;
import com.imane.backend.repository.TeamRepository;
import com.imane.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import com.imane.backend.dto.TeamMemberDTO;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    private User loadManager(Long managerId) {
        if (managerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "managerId is required");
        }
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Manager not found"));
        if (!"MANAGER".equals(manager.getRole().getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is not a MANAGER");
        }
        return manager;
    }

    private TeamDTO toDTO(Team t) {
        User m = t.getManager();
        return new TeamDTO(t.getId(), t.getName(), t.getDescription(),
                m.getId(), m.getFirstName() + " " + m.getLastName());
    }

    @Override
    public TeamDTO createTeam(CreateTeamRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }
        if (teamRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Team name already used");
        }
        Team team = new Team();
        team.setName(request.getName());
        team.setDescription(request.getDescription());
        team.setManager(loadManager(request.getManagerId()));
        return toDTO(teamRepository.save(team));
    }

    @Override
    public List<TeamDTO> getTeamsFor(User me) {
        String role = me.getRole().getName();
        List<Team> teams;
        if ("ADMIN".equals(role)) {
            teams = teamRepository.findAll();
        } else if ("MANAGER".equals(role)) {
            teams = teamRepository.findByManagerId(me.getId());
        } else if ("EMPLOYEE".equals(role) && me.getTeam() != null) {
            teams = List.of(me.getTeam());
        } else {
            teams = List.of();
        }
        return teams.stream().map(this::toDTO).toList();
    }

    @Override
    public TeamDTO getTeamById(Long id, User me) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
        String role = me.getRole().getName();
        boolean allowed =
                "ADMIN".equals(role)
                || ("MANAGER".equals(role) && team.getManager().getId().equals(me.getId()))
                || ("EMPLOYEE".equals(role) && me.getTeam() != null
                    && me.getTeam().getId().equals(team.getId()));
        if (!allowed) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return toDTO(team);
    }

    @Override
    public TeamDTO updateTeam(Long id, UpdateTeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
        if (request.getName() != null && !request.getName().equals(team.getName())) {
            if (teamRepository.existsByName(request.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Team name already used");
            }
            team.setName(request.getName());
        }
        if (request.getDescription() != null) team.setDescription(request.getDescription());
        if (request.getManagerId() != null) team.setManager(loadManager(request.getManagerId()));
        return toDTO(teamRepository.save(team));
    }

    @Override
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
        if (!team.getUsers().isEmpty() || !team.getProjects().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Team has members or projects, remove them first");
        }
        teamRepository.delete(team);
    }
    
@Override
public List<TeamMemberDTO> getTeamMembers(Long teamId, User me) {
    Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Team not found"));

    String role = me.getRole().getName();

    boolean allowed =
            "ADMIN".equals(role)
            || ("MANAGER".equals(role)
                && team.getManager().getId().equals(me.getId()));

    if (!allowed) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Access denied");
    }

    return team.getUsers().stream()
            .filter(user -> "EMPLOYEE".equals(user.getRole().getName()))
            .map(user -> new TeamMemberDTO(
                    user.getId(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail()))
            .toList();
}

}