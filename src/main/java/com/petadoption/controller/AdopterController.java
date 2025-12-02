package com.petadoption.controller;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.AdopterProfileUpdateDto;
import com.petadoption.dto.AdoptionDto;
import com.petadoption.dto.ShelterResponseDto;
import com.petadoption.service.AdopterService;
import com.petadoption.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adopter")
@RequiredArgsConstructor
public class AdopterController {

    private final AdopterService adopterService;
    private final ShelterService shelterService;

    @GetMapping("/me")
    public AdopterDto getProfile(@RequestHeader("Authorization") String authHeader) {
        // extract token from "Bearer <token>"
        String token = authHeader.replace("Bearer ", "");
        return adopterService.getProfileFromToken(token);
    }

    @PutMapping("/update")
    public String updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody AdopterProfileUpdateDto dto) {

        // extract token from "Bearer <token>"
        String token = authHeader.replace("Bearer ", "");
        return adopterService.updateProfile(token, dto);
    }

    @PutMapping("/updatePassword")
    public String updateProfile(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody String password) {

        // extract token from "Bearer <token>"
        String token = authHeader.replace("Bearer ", "");
        return adopterService.updatePassword(token, password);
    }

    @GetMapping("/getAdoptions")
    List<AdoptionDto> getAdoptions(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return adopterService.getAdoptions(token);
    }
}
