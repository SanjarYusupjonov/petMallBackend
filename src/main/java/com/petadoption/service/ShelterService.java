package com.petadoption.service;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.ShelterResponseDto;
import com.petadoption.dto.StaffDto;
import com.petadoption.entity.Shelter;
import com.petadoption.repository.ShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterService {
    private final ShelterRepository shelterRepository;

    @Transactional(readOnly = true)
    public List<ShelterResponseDto> getAll(String token) {
        List<Shelter> shelters = shelterRepository.findAllWithDetails();

        return shelters.stream().map(shelter -> {
            List<ShelterResponseDto.ShelterContact> contacts = shelter.getContacts().stream()
                    .map(c -> ShelterResponseDto.ShelterContact.builder()
                            .contactType(c.getContactType())
                            .value(c.getValue())
                            .build())
                    .toList();

            List<ShelterResponseDto.ShelterWorkingHour> workingHours = shelter.getWorkingHours().stream()
                    .map(w -> ShelterResponseDto.ShelterWorkingHour.builder()
                            .dayOfWeek(w.getDayOfWeek())
                            .openingTime(w.getOpeningTime())
                            .closingTime(w.getClosingTime())
                            .build())
                    .toList();

            List<StaffDto> staffs = shelter.getStaffMembers().stream()
                    .map(staff -> StaffDto.builder()
                            .id(staff.getId())
                            .name(staff.getName())
                            .address(staff.getAddress())
                            .userId(staff.getUser().getId())
                            .build())
                    .toList();

            return ShelterResponseDto.builder()
                    .id(shelter.getId())
                    .name(shelter.getName())
                    .address(shelter.getAddress())
                    .capacity(shelter.getCapacity())
                    .shelterContacts(contacts)
                    .shelterWorkingHours(workingHours)
                    .staffs(staffs)
                    .build();

        }).toList();
    }
}
