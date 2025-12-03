package com.petadoption.service;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.StaffDto;
import com.petadoption.dto.StaffRequestDto;
import com.petadoption.dto.StaffUpdateRequest;
import com.petadoption.entity.Shelter;
import com.petadoption.entity.Staff;
import com.petadoption.entity.User;
import com.petadoption.enums.Role;
import com.petadoption.repository.ShelterRepository;
import com.petadoption.repository.StaffRepository;
import com.petadoption.repository.UserRepository;
import com.petadoption.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.beans.Encoder;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final ShelterRepository shelterRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public StaffDto getProfileFromToken(String token) {
        String email = jwtUtil.parseToken(token).getBody().getSubject();
        StaffDto staff = staffRepository.findStaffByUserEmail(email);
        if (staff == null) {
            throw new RuntimeException("Adopter not found");
        }

        return staff;
    }

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

    public ResponseEntity<?> updateProfile(String token, StaffUpdateRequest request) {
        String email = jwtUtil.parseToken(token).getBody().getSubject();
        StaffDto staff = staffRepository.findStaffByUserEmail(email);
        if (staff == null) {
            throw new RuntimeException("Adopter not found");
        }

        Optional<Staff> staffEntity = staffRepository.findById(staff.getId());

        if (!staffEntity.isPresent()) {
            System.out.println("No staff entity found");
            return ResponseEntity.ofNullable("No staff entity found");
        }

        Staff staff1 = staffEntity.get();
        staff1.setAddress(request.getAddress());
        staff1.setName(request.getName());

        staffRepository.save(staff1);

        return ResponseEntity.ok("Updated");
    }

}
