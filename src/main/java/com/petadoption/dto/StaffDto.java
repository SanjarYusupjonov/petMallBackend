package com.petadoption.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffDto {
    private Long id;
    private String name;
    private String address;
    private Long shelterId;
    private Long userId;

    @Override
    public String toString() {
        return "\n------------------------------\n" +
                "STAFF INFORMATION\n" +
                "------------------------------\n" +
                "ID          : " + id + "\n" +
                "Name        : " + name + "\n" +
                "Address     : " + address + "\n" +
                "Shelter ID  : " + shelterId + "\n" +
                "User ID     : " + userId + "\n" +
                "------------------------------\n";
    }

}
