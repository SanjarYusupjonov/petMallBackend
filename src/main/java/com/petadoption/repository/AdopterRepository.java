package com.petadoption.repository;

import com.petadoption.dto.AdopterDto;
import com.petadoption.entity.Adopter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdopterRepository extends JpaRepository<Adopter, Long> {
    @Query(value = "SELECT new com.petadoption.dto.AdopterDto(a.id, a.name, u.email, a.address, ah.numberOfAdults, ah.numberOfChildren, ah.hasOtherPets) " +
            "FROM Adopter a " +
            "JOIN User u ON u.id = a.user.id " +
            "JOIN AdoptersHousehold ah ON ah.adopter.id = a.id " +
            "WHERE u.email = :email")
    AdopterDto findByEmail(@Param("email") String email);
}
