package com.imane.backend.service;

import java.util.List;

import com.imane.backend.dto.UserDTO;
import com.imane.backend.dto.request.CreateUserRequest;
import com.imane.backend.dto.request.LoginRequest;
import com.imane.backend.dto.request.UpdateUserRequest;
import com.imane.backend.dto.response.LoginResponse;
import com.imane.backend.dto.request.RegisterRequest;
import com.imane.backend.dto.request.ChangeRoleRequest;
import com.imane.backend.dto.request.ChangeTeamRequest;

public interface UserService {

    UserDTO createUser(CreateUserRequest request);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    UserDTO updateUser(Long id, UpdateUserRequest request);
    UserDTO register(RegisterRequest request);

    void deleteUser(Long id);

    LoginResponse login(LoginRequest request);
    UserDTO changeTeam(Long id, ChangeTeamRequest request);

    UserDTO changeRole(Long id, ChangeRoleRequest request, Long adminId); 
}