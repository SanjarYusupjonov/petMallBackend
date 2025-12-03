package com.petadoption.repository;

import com.petadoption.entity.AnimalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnimalStatusRepository extends JpaRepository<AnimalStatus,Long> {

}
