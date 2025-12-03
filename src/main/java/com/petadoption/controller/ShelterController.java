package com.petadoption.controller;

import com.petadoption.dto.ShelterRequestDto;
import com.petadoption.dto.ShelterResponseDto;
import com.petadoption.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shelter")
@RequiredArgsConstructor
public class ShelterController {
    private final ShelterService shelterService;

    @GetMapping("/getAll")
    public List<ShelterResponseDto> getAll() {
        return shelterService.getAll();
    }

    @PostMapping("/create")
    public ResponseEntity<ShelterResponseDto> createShelter(
            @RequestHeader("Authorization") String token,
            @RequestBody ShelterRequestDto shelterRequestDto) {
        ShelterResponseDto createdShelter = shelterService.createShelter(shelterRequestDto, token);
        return new ResponseEntity<>(createdShelter, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateShelter(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody ShelterRequestDto shelterRequestDto) {
        return  shelterService.updateShelter(id, shelterRequestDto, token);
    }
}
