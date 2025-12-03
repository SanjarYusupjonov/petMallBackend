package com.petadoption.controller;

import com.petadoption.dto.AnimalDto;
import com.petadoption.service.AnimalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/animals")
@CrossOrigin
public class AnimalController {

    private final AnimalService animalService;

    @GetMapping
    public ResponseEntity<List<AnimalDto>> getAnimalsByShelterAndStatus(
            @RequestParam Long shelterId,
            @RequestParam(required = false) List<String> status,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Integer age) {

        List<AnimalDto> animals = animalService.getAnimalsByShelterAndStatus(
                shelterId, status, name, species, age);

        return ResponseEntity.ok(animals);
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> isAnimalAvailable(@PathVariable Long id) {
        boolean available = animalService.isAnimalAvailable(id);
        return ResponseEntity.ok(available);
    }
}
