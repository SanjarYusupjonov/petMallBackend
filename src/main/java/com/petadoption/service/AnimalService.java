package com.petadoption.service;

import com.petadoption.dto.AnimalDto;
import com.petadoption.dto.AnimalDtoShelter;
import com.petadoption.dto.AnimalEventRequestDto;
import com.petadoption.entity.Animal;
import com.petadoption.entity.AnimalEvent;
import com.petadoption.entity.AnimalStatus;
import com.petadoption.entity.Shelter;
import com.petadoption.enums.AnimalStatusEnum;
import com.petadoption.repository.AnimalEventRepository;
import com.petadoption.repository.AnimalRepository;
import com.petadoption.repository.AnimalStatusRepository;
import com.petadoption.repository.ShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnimalService {

    private final AnimalRepository animalRepository;
    private final ShelterRepository shelterRepository;
    private final AnimalStatusRepository animalStatusRepository;
    private final AnimalEventRepository animalEventRepository;

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

    public List<AnimalDto> getAllAnimals() {
        List<Animal> animals = animalRepository.findAll();

        return animals.stream()
                .map(animal -> AnimalDto.builder()
                        .id(animal.getId())
                        .name(animal.getName())
                        .species(animal.getSpecies())
                        .breed(animal.getBreed())
                        .sex(animal.getSex())
                        .age(animal.getAge())
                        .weight(animal.getWeight())
                        .color(animal.getColor())
                        .intakeDate(animal.getIntakeDate())
                        .build())
                .collect(Collectors.toList());
    }

    public Animal updateAnimal(Long id, AnimalDto dto) throws Exception {
        Optional<Animal> optionalAnimal = animalRepository.findById(id);
        if (optionalAnimal.isEmpty()) {
            throw new Exception("Animal not found with id: " + id);
        }

        Animal animal = optionalAnimal.get();

        if (dto.getName() != null) animal.setName(dto.getName());
        if (dto.getSpecies() != null) animal.setSpecies(dto.getSpecies());
        if (dto.getBreed() != null) animal.setBreed(dto.getBreed());
        if (dto.getSex() != null) animal.setSex(dto.getSex());
        if (dto.getAge() != null) animal.setAge(dto.getAge());
        if (dto.getWeight() != null) animal.setWeight(dto.getWeight());
        if (dto.getColor() != null) animal.setColor(dto.getColor());
        if (dto.getIntakeDate() != null) animal.setIntakeDate(dto.getIntakeDate());

        return animalRepository.save(animal);
    }

    public Animal addAnimal(AnimalDtoShelter dto) {
        Shelter shelter = shelterRepository.findById(dto.getShelterId())
                .orElseThrow(() -> new RuntimeException("Shelter not found"));
        AnimalStatus status = animalStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        Animal animal = Animal.builder()
                .name(dto.getName())
                .species(dto.getSpecies())
                .breed(dto.getBreed())
                .sex(dto.getSex())
                .age(dto.getAge())
                .weight(dto.getWeight())
                .color(dto.getColor())
                .intakeDate(dto.getIntakeDate())
                .shelter(shelter)
                .status(status)
                .build();

        return animalRepository.save(animal);
    }

    public AnimalEvent addAnimalEvent(AnimalEventRequestDto dto) {
        Animal animal = animalRepository.findById(dto.getAnimalId())
                .orElseThrow(() -> new RuntimeException("Animal not found with ID: " + dto.getAnimalId()));

        LocalDateTime eventDate = LocalDateTime.parse(dto.getEventDate());

        AnimalEvent event = AnimalEvent.builder()
                .animal(animal)
                .eventType(dto.getEventType())
                .eventDate(eventDate)
                .details(dto.getDetails())
                .build();

        return animalEventRepository.save(event);
    }
}
