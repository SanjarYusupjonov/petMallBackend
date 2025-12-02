package com.petadoption.repository;

import com.petadoption.dto.ApplicationResponseDto;
import com.petadoption.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @Query(value = """
        SELECT a.id, an.name as animal_name, an.species as animal_species,
               a.submission_date, aps.status, aps.updated_at
        FROM application a
        JOIN animal an ON a.animal_id = an.id
        JOIN application_status aps ON aps.application_id = a.id
        WHERE a.adopter_id = :adopterId
    """, nativeQuery = true)
    List<Object[]> findRawByAdopterId(@Param("adopterId") Long adopterId);
}
