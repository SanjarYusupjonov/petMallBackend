package com.petadoption.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdopterDto {
    private Long id;
    private String name;
    private String email;
    private String address;
    private Integer numberOfAdults;
    private Integer numberOfChildren;
    private Boolean hasOtherPets;
}
