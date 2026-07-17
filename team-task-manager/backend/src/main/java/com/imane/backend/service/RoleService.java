package com.imane.backend.service;

import java.util.List;
import java.util.Optional;

import com.imane.backend.entity.Role;

public interface RoleService {

    Role createRole(Role role);

    List<Role> getAllRoles();

    Optional<Role> getRoleById(Long id);

    Role updateRole(Long id, Role role);

    void deleteRole(Long id);
}