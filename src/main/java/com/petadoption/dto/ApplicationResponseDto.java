package com.petadoption.dto;

import com.petadoption.enums.ApplicationStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponseDto {
    private Long id;
    private String animalName;
    private String animalSpecies;
    private java.util.Date submissionDate;   // match entity
    private ApplicationStatusEnum status; // match entity
    private java.util.Date statusUpdatedDate; // match entity
}

