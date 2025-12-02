package com.petadoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDto {
    private Long id;
    private String animalName;
    private String animalSpecies;
    private java.util.Date submissionDate;   // match entity
    private com.petadoption.enums.Status status; // match entity
    private java.util.Date statusUpdatedDate; // match entity
}

