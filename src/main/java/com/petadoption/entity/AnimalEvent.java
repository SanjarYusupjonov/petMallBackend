package com.petadoption.entity;

import com.petadoption.enums.AnimalEventType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnimalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Enumerated(EnumType.STRING)
    private AnimalEventType eventType;

    private LocalDateTime eventDate;  // TIMESTAMP

    @Column(columnDefinition = "TEXT")
    private String details;  // Optional description (vaccine name, notes, etc.)
}
