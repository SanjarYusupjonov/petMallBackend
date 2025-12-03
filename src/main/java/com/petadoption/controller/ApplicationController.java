package com.petadoption.controller;

import com.petadoption.dto.ApplicationResponseDto;
import com.petadoption.dto.ApplicationResponseDtoStaff;
import com.petadoption.enums.ApplicationStatusEnum;
import com.petadoption.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/getAllApplications")
    List<ApplicationResponseDtoStaff> getAllApplications(@RequestHeader("Authorization") String authHeader){
        String token = authHeader.replace("Bearer ", "");
        return applicationService.getAllApplicationsRaw(token);
    }

    @PutMapping("/update-status")
    public ResponseEntity<?> updateApplicationStatus(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, Object> payload) {

        String token = authHeader.replace("Bearer ", "");

        Long applicationId = Long.valueOf(payload.get("applicationId").toString());
        ApplicationStatusEnum status = ApplicationStatusEnum.valueOf(payload.get("status").toString());

        return applicationService.updateApplicationStatus(token, applicationId, status);
    }


}
