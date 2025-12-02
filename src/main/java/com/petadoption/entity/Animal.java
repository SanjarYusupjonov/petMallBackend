package com.petadoption.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "shelter_id", referencedColumnName = "id")
    private Shelter shelter;

    private String name;
    private String species;
    private String breed;
    private String sex;
    private Integer age;       // in years
    private Double weight;     // in kg
    private String color;

    @Column(name = "intake_date")
    private LocalDate intakeDate;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private AnimalStatus status;
}
