package com.petadoption.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelterResponseDto {
    private Long id;
    private String name;
    private String address;
    private List<ShelterContact> shelterContacts;
    private Long capacity;
    private List<ShelterWorkingHour> shelterWorkingHours;
    private List<StaffDto> staffs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShelterContact {
        private String contactType;
        private String value;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShelterWorkingHour {
        private String dayOfWeek;

        @JsonFormat(pattern = "HH:mm")
        private LocalTime openingTime;

        @JsonFormat(pattern = "HH:mm")
        private LocalTime closingTime;
    }
}
