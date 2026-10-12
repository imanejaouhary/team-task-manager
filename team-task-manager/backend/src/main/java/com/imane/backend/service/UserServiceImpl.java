package com.imane.backend.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.imane.backend.dto.UserDTO;
import com.imane.backend.dto.request.CreateUserRequest;
import com.imane.backend.dto.request.LoginRequest;
import com.imane.backend.dto.response.LoginResponse;
import com.imane.backend.entity.Role;
import com.imane.backend.entity.Team;
import com.imane.backend.entity.User;
import com.imane.backend.mapper.UserMapper;
import com.imane.backend.repository.RoleRepository;
import com.imane.backend.repository.TeamRepository;
import com.imane.backend.repository.UserRepository;
import com.imane.backend.security.JwtService;
import com.imane.backend.dto.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import com.imane.backend.dto.request.RegisterRequest ;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserDTO createUser(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
        }

        if (request.getRoleName() == null || request.getRoleName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "roleName is required");
        }

        String roleName = request.getRoleName().trim().toUpperCase();

        if ("ADMIN".equals(roleName)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot create ADMIN");
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Role not found"
                ));

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        if ("EMPLOYEE".equals(roleName)) {
            if (request.getTeamId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "teamId is required for EMPLOYEE"
                );
            }
            Team team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Team not found"
                    ));
            user.setTeam(team);
        } else if (request.getTeamId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "teamId is only allowed for EMPLOYEE"
            );
        }

        User savedUser = userRepository.save(user);

        return UserMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return UserMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    @Override
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
    User existing = userRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    if (request.getEmail() != null && !request.getEmail().equals(existing.getEmail())) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
        }
        existing.setEmail(request.getEmail());
    }
    if (request.getFirstName() != null) existing.setFirstName(request.getFirstName());
    if (request.getLastName() != null) existing.setLastName(request.getLastName());

    return UserMapper.toDTO(userRepository.save(existing));
}

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"
                ));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(token);
    }
    @Override
public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
    }
    userRepository.deleteById(id);
}
@Override
public UserDTO register(RegisterRequest request) {

    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already used");
    }

    Role clientRole = roleRepository.findByName("CLIENT")
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Role CLIENT not configured"));

    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(clientRole);   // hardcodé f server

    return UserMapper.toDTO(userRepository.save(user));
}
}