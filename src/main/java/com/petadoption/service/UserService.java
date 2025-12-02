package com.petadoption.service;

import com.petadoption.entity.User;
import com.petadoption.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByUsername(String email) {
        return userRepository.findByEmail(email);
    }

    // Additional user-related operations can go here
}
