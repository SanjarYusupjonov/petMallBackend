package com.petadoption.service;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.ApplicationResponseDto;
import com.petadoption.enums.Status;
import com.petadoption.repository.AdopterRepository;
import com.petadoption.repository.ApplicationRepository;
import com.petadoption.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final JwtUtil jwtUtil;
    private final AdopterRepository adopterRepository;

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
                        .status((Status) r[4])
                        .statusUpdatedDate((Date) r[5])
                        .build())
                .toList();
    }
}
