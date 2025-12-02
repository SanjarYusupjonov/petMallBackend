package com.petadoption.repository;

import com.petadoption.entity.AdoptersHousehold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdoptersHouseholdRepository extends JpaRepository<AdoptersHousehold, Long> {
}
