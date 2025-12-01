package com.petadoption.service;

import com.petadoption.dto.LoginRequest;
import com.petadoption.dto.LoginResponse;
import com.petadoption.dto.SignupRequest;
import com.petadoption.entity.User;
import com.petadoption.enums.Role;
import com.petadoption.repository.UserRepository;
import com.petadoption.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    // Signup for ADOPTER only
    public String signup(SignupRequest req) {
        if (userRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(req.getUsername())
                .password(encoder.encode(req.getPassword()))
                .role(Role.ADOPTER)
                .build();

        userRepository.save(user);
        return "Adopter registered successfully";
    }

    // Login for both ADOPTER and STAFF
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new LoginResponse(token, user.getRole());
    }
}
