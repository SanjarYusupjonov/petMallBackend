package com.petadoption.entity;

import com.petadoption.enums.AnimalStatusEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private AnimalStatusEnum name; // AVAILABLE, ADOPTED, PENDING
}
