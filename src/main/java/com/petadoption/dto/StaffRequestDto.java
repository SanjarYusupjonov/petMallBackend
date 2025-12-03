package com.petadoption.dto;

import lombok.Data;

@Data
public class StaffRequestDto {
    private String name;       // Staff full name
    private String address;    // Staff address
    private String email;      // User email
    private String password;   // User password
    private Long shelterId;    // Shelter the staff belongs to
}
