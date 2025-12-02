package com.petadoption.service;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.AdopterProfileUpdateDto;
import com.petadoption.dto.AdoptionDto;
import com.petadoption.entity.Adopter;
import com.petadoption.entity.AdoptersHousehold;
import com.petadoption.entity.User;
import com.petadoption.repository.AdopterRepository;
import com.petadoption.repository.AdoptionRepository;
import com.petadoption.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdopterService {

    private final AdopterRepository adopterRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;
    private final AdoptionRepository adoptionRepository;

    public AdopterDto getProfileFromToken(String token) {
        String email = jwtUtil.parseToken(token).getBody().getSubject();
        AdopterDto adopter = adopterRepository.findByEmail(email);
        if (adopter == null) {
            throw new RuntimeException("Adopter not found");
        }

        return adopter;
    }

    public String updateProfile(String token, AdopterProfileUpdateDto dto) {
        String email = jwtUtil.parseToken(token).getBody().getSubject();
        AdopterDto adopter = adopterRepository.findByEmail(email);
        if (adopter == null) {
            throw new RuntimeException("Adopter not found");
        }

        Adopter adopter1 = adopterRepository.findById(adopter.getId()).get();

        adopter1.setAddress(dto.getAddress());

        AdoptersHousehold household = adopter1.getHousehold();
        household.setNumberOfAdults(dto.getNumberOfAdults());
        household.setHasOtherPets(dto.getHasOtherPets());
        household.setNumberOfChildren(dto.getNumberOfChildren());

        adopter1.setHousehold(household);

        adopterRepository.save(adopter1);

        return "Updated";
    }

    public String updatePassword(String token, String password) {
        String email = jwtUtil.parseToken(token).getBody().getSubject();
        AdopterDto adopter = adopterRepository.findByEmail(email);
        if (adopter == null) {
            throw new RuntimeException("Adopter not found");
        }

        Adopter adopter1 = adopterRepository.findById(adopter.getId()).get();

        User user = adopter1.getUser();
        user.setPassword(encoder.encode(password));

        adopter1.setUser(user);

        adopterRepository.save(adopter1);

        return "Updated";
    }

    public List<AdoptionDto> getAdoptions(String token) {
        // Find adopter by token
        String email = jwtUtil.parseToken(token).getBody().getSubject();
        AdopterDto adopter = adopterRepository.findByEmail(email);
        if (adopter == null) {
            throw new RuntimeException("Adopter not found");
        }

        List<Object[]> rows = adoptionRepository.findAdoptionsByAdopterId(adopter.getId());

        List<AdoptionDto> result = new ArrayList<>();
        for (Object[] row : rows) {
            Long adoptionId = ((Number) row[0]).longValue();
            Long applicationId = ((Number) row[1]).longValue();

            // Convert SQL Date to LocalDate
            LocalDate date;
            if (row[2] instanceof java.sql.Date) {
                date = ((java.sql.Date) row[2]).toLocalDate();
            } else if (row[2] instanceof java.time.LocalDate) {
                date = (LocalDate) row[2];
            } else {
                date = null;
            }

            // Convert fee to Double safely
            Double fee = null;
            if (row[3] != null) {
                if (row[3] instanceof BigDecimal) {
                    fee = ((BigDecimal) row[3]).doubleValue();
                } else if (row[3] instanceof Number) {
                    fee = ((Number) row[3]).doubleValue();
                }
            }

            result.add(new AdoptionDto(adoptionId, applicationId, date, fee));
        }
        return result;
    }

}
