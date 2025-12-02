package com.petadoption.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdoptersHousehold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "adopter_id", nullable = false)
    private Adopter adopter;

    private int numberOfAdults;
    private int numberOfChildren;

    private boolean hasOtherPets;
}
