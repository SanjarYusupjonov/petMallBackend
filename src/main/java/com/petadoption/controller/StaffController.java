package com.petadoption.controller;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.StaffDto;
import com.petadoption.dto.StaffRequestDto;
import com.petadoption.dto.StaffUpdateRequest;
import com.petadoption.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping("/create")
    public ResponseEntity<StaffDto> createStaff(
            @RequestHeader("Authorization") String token,
            @RequestBody StaffRequestDto requestDto) {

        // Token can be validated in service if needed
        StaffDto createdStaff = staffService.createStaff(requestDto);
        return new ResponseEntity<>(createdStaff, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public StaffDto getProfile(@RequestHeader("Authorization") String authHeader) {
        // extract token from "Bearer <token>"
        String token = authHeader.replace("Bearer ", "");
        return staffService.getProfileFromToken(token);
    }

    @PutMapping("/update/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody StaffUpdateRequest request) {

        String token = authHeader.replace("Bearer ", "");

        return staffService.updateProfile(token, request);
    }
}
