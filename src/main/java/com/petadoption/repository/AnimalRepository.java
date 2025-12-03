package com.petadoption.repository;

import com.petadoption.entity.Animal;
import com.petadoption.enums.AnimalStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
        @Query("""
       SELECT a FROM Animal a
       JOIN a.status s
       WHERE a.shelter.id = :shelterId
         AND (:statuses IS NULL OR s.name IN :statuses)
         AND (:name IS NULL OR LOWER(a.name) LIKE (CONCAT('%', :name, '%')))
         AND (:species IS NULL OR LOWER(a.species) LIKE (CONCAT('%', :species, '%')))
         AND (:age IS NULL OR a.age = :age)
       """)
        List<Animal> findByFilters(
                @Param("shelterId") Long shelterId,
                @Param("statuses") List<AnimalStatusEnum> statuses,
                @Param("name") String name,
                @Param("species") String species,
                @Param("age") Integer age
        );
    }


