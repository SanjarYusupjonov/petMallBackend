package com.petadoption.repository;

import com.petadoption.entity.AnimalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalEventRepository extends JpaRepository<AnimalEvent, Long> {

}
