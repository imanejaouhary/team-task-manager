package com.imane.backend.service;

import java.util.List;
import java.util.Optional;

import com.imane.backend.entity.Team;

public interface TeamService {

    Team createTeam(Team team);

    List<Team> getAllTeams();

    Optional<Team> getTeamById(Long id);

    Team updateTeam(Long id, Team team);

    void deleteTeam(Long id);
}