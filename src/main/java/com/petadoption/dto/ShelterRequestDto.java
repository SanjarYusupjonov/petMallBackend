package com.petadoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelterRequestDto {
    private String name;
    private String address;
    private Long capacity;
    private List<ShelterContactDto> contacts;
    private List<ShelterWorkingHourDto> workingHours;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShelterContactDto {
        private String contactType;
        private String value;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShelterWorkingHourDto {
        private String dayOfWeek;
        private LocalTime openingTime;
        private LocalTime closingTime;
    }
}
