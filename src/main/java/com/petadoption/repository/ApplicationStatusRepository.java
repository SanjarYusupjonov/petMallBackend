package com.petadoption.repository;

import com.petadoption.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus,Long> {

}
