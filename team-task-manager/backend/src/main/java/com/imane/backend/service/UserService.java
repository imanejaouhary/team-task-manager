package com.imane.backend.service;

import java.util.List;

import com.imane.backend.dto.UserDTO;
import com.imane.backend.dto.request.CreateUserRequest;
import com.imane.backend.entity.User;

public interface UserService {

    UserDTO createUser(CreateUserRequest request);

    UserDTO getUserById(Long id);

    List<UserDTO> getAllUsers();

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}