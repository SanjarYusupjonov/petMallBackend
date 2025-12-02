package com.petadoption.service;

import com.petadoption.dto.LoginRequest;
import com.petadoption.dto.LoginResponse;
import com.petadoption.dto.SignupRequest;
import com.petadoption.entity.Adopter;
import com.petadoption.entity.AdoptersHousehold;
import com.petadoption.entity.User;
import com.petadoption.enums.Role;
import com.petadoption.repository.AdopterRepository;
import com.petadoption.repository.AdoptersHouseholdRepository;
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
    private final AdopterRepository adopterRepository;
    private final AdoptersHouseholdRepository adoptersHouseholdRepository;

    // Signup for ADOPTER only
    public LoginResponse signup(SignupRequest req) {
        if (userRepository.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Create User
        User user = User.builder()
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(Role.ADOPTER)
                .build();
        userRepository.saveAndFlush(user);

        // Create Adopter
        Adopter adopter = Adopter.builder()
                .address(req.getAddress())
                .name(req.getName())
                .user(user)
                .build();
        adopterRepository.save(adopter);

        // Create Household details
        AdoptersHousehold household = AdoptersHousehold.builder()
                .adopter(adopter)
                .numberOfAdults(req.getNumberOfAdults())
                .numberOfChildren(req.getNumberOfChildren())
                .hasOtherPets(req.getHasOtherPets())
                .build();

        adoptersHouseholdRepository.save(household);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getRole());
    }


    // Login for both ADOPTER and STAFF
    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token, user.getRole());
    }
}
