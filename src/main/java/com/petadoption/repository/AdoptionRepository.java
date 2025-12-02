package com.petadoption.repository;

import com.petadoption.entity.Adoption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {
    @Query(
            value = "SELECT a.id, a2.id AS application_id, a.date, a.fee " +
                    "FROM adoption a " +
                    "JOIN application a2 ON a2.id = a.application_id " +
                    "WHERE a2.adopter_id = :adopterId",
            nativeQuery = true
    )
    List<Object[]> findAdoptionsByAdopterId(@Param("adopterId") Long adopterId);

}
