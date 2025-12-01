package com.petadoption.config;

import com.petadoption.entity.User;
import com.petadoption.enums.Role;
import com.petadoption.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder encoder;

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("staff1").isEmpty()) {
                User staff = User.builder()
                        .username("staff1")
                        .password(encoder.encode("password123"))
                        .role(Role.STAFF)
                        .build();
                userRepository.save(staff);
            }
        };
    }
}
