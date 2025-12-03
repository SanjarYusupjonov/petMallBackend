package com.petadoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalDtoShelter {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String sex;
    private Integer age;
    private Double weight;
    private String color;
    private LocalDate intakeDate;
    private Long statusId;
    private Long shelterId;
}
