package com.petadoption.service;

import com.petadoption.dto.AnimalDto;
import com.petadoption.entity.Animal;
import com.petadoption.enums.AnimalStatusEnum;
import com.petadoption.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;

    public List<AnimalDto> getAnimalsByShelterAndStatus(Long shelterId, List<String> statuses,
                                                        String name, String species, Integer age) {

        if (name != null){
            name = name.toLowerCase(Locale.ROOT);
        }else {
            name = "";
        }
        if (species != null){
            species = species.toLowerCase(Locale.ROOT);
        }else {
            species = "";
        }

        List<AnimalStatusEnum> statusEnums = null;
        if (statuses != null && !statuses.isEmpty()) {
            statusEnums = statuses.stream()
                    .map(String::toUpperCase)
                    .map(AnimalStatusEnum::valueOf)
                    .toList();
        }

        List<Animal> animals = animalRepository.findByFilters(
                shelterId,
                statusEnums,
                name,
                species,
                age
        );

        return animals.stream()
                .map(a -> AnimalDto.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .species(a.getSpecies())
                        .breed(a.getBreed())
                        .sex(a.getSex())
                        .age(a.getAge())
                        .weight(a.getWeight())
                        .color(a.getColor())
                        .intakeDate(a.getIntakeDate())
                        .build())
                .toList();
    }

    public boolean isAnimalAvailable(Long animalId) {
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal not found"));
        return animal.getStatus().getName() == AnimalStatusEnum.AVAILABLE;
    }

}
