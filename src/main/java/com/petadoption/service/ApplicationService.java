package com.petadoption.service;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.ApplicationResponseDto;
import com.petadoption.entity.Adopter;
import com.petadoption.entity.Animal;
import com.petadoption.entity.Application;
import com.petadoption.entity.ApplicationStatus;
import com.petadoption.enums.AnimalStatusEnum;
import com.petadoption.enums.ApplicationStatusEnum;
import com.petadoption.repository.AdopterRepository;
import com.petadoption.repository.AnimalRepository;
import com.petadoption.repository.ApplicationRepository;
import com.petadoption.repository.ApplicationStatusRepository;
import com.petadoption.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JwtUtil jwtUtil;
    private final AdopterRepository adopterRepository;
    private final AnimalRepository animalRepository;
    private final ApplicationStatusRepository applicationStatusRepository;

    public List<ApplicationResponseDto> getAll(String token) {
        String email = jwtUtil.parseToken(token).getBody().getSubject();
        AdopterDto adopter = adopterRepository.findByEmail(email);
        if (adopter == null) {
            throw new RuntimeException("Adopter not found");
        }
        List<Object[]> rows = applicationRepository.findRawByAdopterId(adopter.getId());

        return rows.stream().map(r -> ApplicationResponseDto.builder()
                        .id(((Number) r[0]).longValue())
                        .animalName((String) r[1])
                        .animalSpecies((String) r[2])
                        .submissionDate((Date) r[3])
                        .status(ApplicationStatusEnum.valueOf((String) r[4]))
                        .statusUpdatedDate((Date) r[5])
                        .build())
                .toList();
    }

    public void applyForAdoption(Long animalId, String jwtToken) {
        String email = jwtUtil.parseToken(jwtToken).getBody().getSubject();
        AdopterDto adopter = adopterRepository.findByEmail(email);
        if (adopter == null) {
            throw new RuntimeException("Adopter not found");
        }

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new RuntimeException("Animal not found"));

        Optional<Adopter> adopterEntity = Optional.ofNullable(adopterRepository.findById(adopter.getId()).orElseThrow(() -> new RuntimeException("Adopter not found")));

        Application application = Application.builder()
                .animal(animal)
                .adopter(adopterEntity.get())
                .build();

        applicationRepository.saveAndFlush(application);

        ApplicationStatus applicationStatus = new ApplicationStatus();
        applicationStatus.setApplication(application);
        applicationStatus.setStatus(ApplicationStatusEnum.PENDING);

        applicationStatusRepository.save(applicationStatus);
    }

}
