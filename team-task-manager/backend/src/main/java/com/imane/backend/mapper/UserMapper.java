package com.imane.backend.mapper;

import com.imane.backend.dto.UserDTO;
import com.imane.backend.entity.User;

public class UserMapper {

    public static UserDTO toDTO(User user) {

        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .teamName(
                        user.getTeam() != null
                                ? user.getTeam().getName()
                                : null
                )
                .build();
    }
}