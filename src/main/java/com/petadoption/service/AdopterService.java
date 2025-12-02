package com.petadoption.service;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.AdopterProfileUpdateDto;
import com.petadoption.entity.Adopter;
import com.petadoption.entity.AdoptersHousehold;
import com.petadoption.entity.User;
import com.petadoption.repository.AdopterRepository;
import com.petadoption.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdopterService {

    private final AdopterRepository adopterRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder encoder;

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

}
