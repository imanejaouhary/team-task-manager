package com.imane.backend.service;

import java.util.List;
import com.imane.backend.dto.TeamDTO;
import com.imane.backend.dto.request.CreateTeamRequest;
import com.imane.backend.dto.request.UpdateTeamRequest;
import com.imane.backend.entity.User;

public interface TeamService {
    TeamDTO createTeam(CreateTeamRequest request);
    List<TeamDTO> getTeamsFor(User currentUser);
    TeamDTO getTeamById(Long id, User currentUser);
    TeamDTO updateTeam(Long id, UpdateTeamRequest request);
    void deleteTeam(Long id);
}