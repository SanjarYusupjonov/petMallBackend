package com.petadoption.controller;

import com.petadoption.dto.ShelterResponseDto;
import com.petadoption.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/shelter")
@RequiredArgsConstructor
public class ShelterController {
    private final ShelterService shelterService;

    @GetMapping("/getAll")
    public List<ShelterResponseDto> getAll() {
        // extract token from "Bearer <token>"
//        String token = authHeader.replace("Bearer ", "");
        return shelterService.getAll();
    }
}
