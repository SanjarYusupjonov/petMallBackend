package com.petadoption.controller;

import com.petadoption.dto.AnimalEventRequestDto;
import com.petadoption.entity.AnimalEvent;
import com.petadoption.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/animal-events")
@RequiredArgsConstructor
public class AnimalEventController {

    private final AnimalService animalService;

    @PostMapping("/add")
    public ResponseEntity<AnimalEvent> addAnimalEvent(@RequestBody AnimalEventRequestDto dto) {
        AnimalEvent savedEvent = animalService.addAnimalEvent(dto);
        return ResponseEntity.ok(savedEvent);
    }
}
