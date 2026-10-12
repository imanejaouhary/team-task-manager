package com.imane.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.imane.backend.dto.TeamDTO;
import com.imane.backend.dto.request.CreateTeamRequest;
import com.imane.backend.dto.request.UpdateTeamRequest;
import com.imane.backend.security.CustomUserDetails;
import com.imane.backend.service.TeamService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamDTO> create(@RequestBody CreateTeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public List<TeamDTO> getAll(@AuthenticationPrincipal CustomUserDetails me) {
        return teamService.getTeamsFor(me.getUser());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public TeamDTO getById(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails me) {
        return teamService.getTeamById(id, me.getUser());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public TeamDTO update(@PathVariable Long id, @RequestBody UpdateTeamRequest request) {
        return teamService.updateTeam(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        teamService.deleteTeam(id);
    }
}