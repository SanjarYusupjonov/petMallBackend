package com.petadoption.service;

import com.petadoption.dto.StaffDto;
import com.petadoption.dto.StaffRequestDto;
import com.petadoption.entity.Shelter;
import com.petadoption.entity.Staff;
import com.petadoption.entity.User;
import com.petadoption.enums.Role;
import com.petadoption.repository.ShelterRepository;
import com.petadoption.repository.StaffRepository;
import com.petadoption.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Encoder;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final ShelterRepository shelterRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public StaffDto createStaff(StaffRequestDto requestDto) {
        // Find shelter
        Shelter shelter = shelterRepository.findById(requestDto.getShelterId())
                .orElseThrow(() -> new RuntimeException("Shelter not found"));

        // Create user
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(encoder.encode(requestDto.getPassword()))
                .role(Role.STAFF)
                .build();
        userRepository.save(user);

        // Create staff
        Staff staff = Staff.builder()
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .shelter(shelter)
                .user(user)
                .build();
        staffRepository.save(staff);

        // Return response DTO
        return StaffDto.builder()
                .id(staff.getId())
                .name(staff.getName())
                .address(staff.getAddress())
                .shelterId(shelter.getId())
                .userId(user.getId())
                .build();
    }
}
