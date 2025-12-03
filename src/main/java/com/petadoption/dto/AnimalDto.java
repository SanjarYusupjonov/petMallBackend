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
public class AnimalDto {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private String sex;
    private Integer age;       // in years
    private Double weight;     // in kg
    private String color;
    private LocalDate intakeDate;
}
