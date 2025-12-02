package com.petadoption.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShelterContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shelter_id")
    private Shelter shelter;

    private String contactType; // e.g., "phone", "email", "fax"
    private String value;       // the actual contact info
}
