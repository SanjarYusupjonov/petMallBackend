package com.petadoption.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String name;
    private String address;
    private String email;
    private String password;

    // Household details
    private Integer numberOfAdults;
    private Integer numberOfChildren;
    private Boolean hasOtherPets;
}
