package com.imane.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.imane.backend.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
