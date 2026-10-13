package com.imane.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.imane.backend.entity.Role;
import com.imane.backend.entity.User;
import com.imane.backend.repository.RoleRepository;
import com.imane.backend.repository.UserRepository;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository,
                                RoleRepository roleRepository,
                                PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "admin@app.com";

            if (userRepository.findByEmail(email).isPresent()) {
                return;
            }

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseThrow(() -> new IllegalStateException(
                            "Role ADMIN absent de la table roles"));

            User admin = new User();
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setRole(adminRole);

            userRepository.save(admin);
        };
    }
}