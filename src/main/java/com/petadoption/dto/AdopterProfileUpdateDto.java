package com.petadoption.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdopterProfileUpdateDto {
    private String address;
    private Integer numberOfAdults;
    private Integer numberOfChildren;
    private Boolean hasOtherPets;
}
