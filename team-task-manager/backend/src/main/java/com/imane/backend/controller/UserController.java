package com.imane.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.imane.backend.dto.UserDTO;
import com.imane.backend.dto.request.CreateUserRequest;
import com.imane.backend.dto.request.LoginRequest;
import com.imane.backend.dto.request.UpdateUserRequest;
import com.imane.backend.dto.response.LoginResponse;
import com.imane.backend.security.CustomUserDetails;
import com.imane.backend.service.UserService;
import com.imane.backend.dto.request.RegisterRequest ;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.imane.backend.dto.request.ChangeTeamRequest;
import com.imane.backend.dto.request.ChangeRoleRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDTO userDTO = userService.getUserById(userDetails.getUser().getId());
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.user.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,
                                              @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }
        @PutMapping("/{id}/team")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeTeam(@PathVariable Long id,
                                              @RequestBody ChangeTeamRequest request) {
        return ResponseEntity.ok(userService.changeTeam(id, request));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> changeRole(@PathVariable Long id,
                                              @RequestBody ChangeRoleRequest request,
                                              @AuthenticationPrincipal CustomUserDetails me) {
        return ResponseEntity.ok(userService.changeRole(id, request, me.getUser().getId()));
    }
}