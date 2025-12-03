package com.petadoption.entity;

import com.petadoption.enums.ApplicationStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "id")
    private Application application;

    @Enumerated(EnumType.STRING)
    private ApplicationStatusEnum status;  // e.g., PENDING, APPROVED, REJECTED

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
}
