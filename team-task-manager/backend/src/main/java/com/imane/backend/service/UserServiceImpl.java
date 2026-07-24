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
import com.imane.backend.entity.Team;
import com.imane.backend.entity.User;
import com.imane.backend.mapper.UserMapper;
import com.imane.backend.repository.TeamRepository;
import com.imane.backend.repository.UserRepository;
import com.imane.backend.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Override
    public UserDTO createUser(CreateUserRequest request) {
    
        User user = new User();
    
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Team team = teamRepository.findById(request.getTeamId())
        .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Team not found"
        ));

        user.setTeam(team);
    
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
    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }
    @Override
public LoginResponse login(LoginRequest request) {

    User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            ));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Invalid password"
        );
    }

    String token = jwtService.generateToken(user);

return new LoginResponse(token);
}
}
