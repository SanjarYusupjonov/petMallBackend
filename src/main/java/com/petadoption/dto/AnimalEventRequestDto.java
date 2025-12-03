package com.petadoption.dto;

import com.petadoption.enums.AnimalEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalEventRequestDto {
    private Long animalId;
    private AnimalEventType eventType;
    private String eventDate;
    private String details;
}
