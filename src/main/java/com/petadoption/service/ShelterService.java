package com.petadoption.service;

import com.petadoption.dto.AdopterDto;
import com.petadoption.dto.ShelterRequestDto;
import com.petadoption.dto.ShelterResponseDto;
import com.petadoption.dto.StaffDto;
import com.petadoption.entity.Shelter;
import com.petadoption.entity.ShelterContact;
import com.petadoption.entity.ShelterWorkingHour;
import com.petadoption.entity.User;
import com.petadoption.enums.Role;
import com.petadoption.repository.ShelterRepository;
import com.petadoption.repository.UserRepository;
import com.petadoption.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShelterService {
    private final ShelterRepository shelterRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ShelterResponseDto> getAll() {
        List<Shelter> shelters = shelterRepository.findAll(); // simple fetch

        // initialize collections to avoid lazy loading issues
        shelters.forEach(s -> {
            s.getContacts().size();
            s.getWorkingHours().size();
            s.getStaffMembers().forEach(st -> st.getUser().getId());
        });

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


    @Transactional
    public ShelterResponseDto createShelter(ShelterRequestDto dto, String token) {
        // validate manager JWT
        String email = jwtUtil.parseToken(token.replace("Bearer ", "")).getBody().getSubject();
        User manager = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Manager not found"));
        if (manager.getRole() != Role.MANAGER) {
            throw new RuntimeException("Unauthorized: Only manager can create shelters");
        }

        Shelter shelter = Shelter.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .capacity(dto.getCapacity())
                .build();

        // add contacts
        if (dto.getContacts() != null) {
            List<ShelterContact> contacts = dto.getContacts().stream()
                    .map(c -> ShelterContact.builder()
                            .contactType(c.getContactType())
                            .value(c.getValue())
                            .shelter(shelter)
                            .build())
                    .toList();
            shelter.setContacts(contacts);
        }

        // add working hours
        if (dto.getWorkingHours() != null) {
            List<ShelterWorkingHour> hours = dto.getWorkingHours().stream()
                    .map(h -> ShelterWorkingHour.builder()
                            .dayOfWeek(h.getDayOfWeek())
                            .openingTime(h.getOpeningTime())
                            .closingTime(h.getClosingTime())
                            .shelter(shelter)
                            .build())
                    .toList();
            shelter.setWorkingHours(hours);
        }

        Shelter savedShelter = shelterRepository.save(shelter);

        return ShelterResponseDto.builder()
                .id(savedShelter.getId())
                .name(savedShelter.getName())
                .address(savedShelter.getAddress())
                .capacity(savedShelter.getCapacity())
                .build();
    }

    public ResponseEntity<?> updateShelter(Long id, ShelterRequestDto dto, String token) {
        // Validate JWT token if needed
        String email = jwtUtil.parseToken(token.replace("Bearer ", "")).getBody().getSubject();
        User manager = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Manager not found"));
        if (manager.getRole() != Role.MANAGER) {
            throw new RuntimeException("Unauthorized: Only manager can update shelters");
        }

        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelter not found with id: " + id));

        // Update basic fields
        if (dto.getName() != null) shelter.setName(dto.getName());
        if (dto.getAddress() != null) shelter.setAddress(dto.getAddress());
        if (dto.getCapacity() != null) shelter.setCapacity(dto.getCapacity());

        // Update contacts
        if (dto.getContacts() != null) {
            shelter.getContacts().clear();
            List<ShelterContact> contacts = dto.getContacts().stream().map(c -> {
                ShelterContact contact = new ShelterContact();
                contact.setContactType(c.getContactType());
                contact.setValue(c.getValue());
                contact.setShelter(shelter);
                return contact;
            }).collect(Collectors.toList());
            shelter.getContacts().addAll(contacts);
        }

        // Update working hours
        if (dto.getWorkingHours() != null) {
            shelter.getWorkingHours().clear();
            List<ShelterWorkingHour> workingHours = dto.getWorkingHours().stream().map(w -> {
                ShelterWorkingHour hour = new ShelterWorkingHour();
                hour.setDayOfWeek(w.getDayOfWeek());
                hour.setOpeningTime(w.getOpeningTime());
                hour.setClosingTime(w.getClosingTime());
                hour.setShelter(shelter);
                return hour;
            }).collect(Collectors.toList());
            shelter.getWorkingHours().addAll(workingHours);
        }

        Shelter updatedShelter = shelterRepository.save(shelter);

        // Map entity to response DTO
        return ResponseEntity.ok("Updated");
    }
}
