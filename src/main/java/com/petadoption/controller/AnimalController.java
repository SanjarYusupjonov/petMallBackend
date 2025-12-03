package com.petadoption.controller;

import com.petadoption.dto.AnimalDto;
import com.petadoption.dto.AnimalDtoShelter;
import com.petadoption.entity.Animal;
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

    @GetMapping("/all")
    public ResponseEntity<List<AnimalDto>> seeAllAnimals() {
        List<AnimalDto> animals = animalService.getAllAnimals();
        return ResponseEntity.ok(animals);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAnimal(@PathVariable Long id, @RequestBody AnimalDto dto) {
        try {
            Animal updatedAnimal = animalService.updateAnimal(id, dto);
            return ResponseEntity.ok(updatedAnimal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addAnimal(@RequestBody AnimalDtoShelter dto) {
        try {
            Animal animal = animalService.addAnimal(dto);
            return ResponseEntity.ok(animal);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to add animal: " + e.getMessage());
        }
    }
}
