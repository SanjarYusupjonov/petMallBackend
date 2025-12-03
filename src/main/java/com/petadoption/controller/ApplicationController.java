package com.petadoption.controller;

import com.petadoption.dto.ApplicationResponseDto;
import com.petadoption.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    @GetMapping("/getAll")
    List<ApplicationResponseDto> getAll(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        return applicationService.getAll(token);
    }

    @PostMapping
    public ResponseEntity<String> applyForAdoption(
            @RequestParam Long animalId,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        applicationService.applyForAdoption(animalId, token);

        return ResponseEntity.ok("Application submitted successfully!");
    }

}
