package com.petadoption.repository;

import com.petadoption.dto.StaffDto;
import com.petadoption.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    @Query("SELECT new com.petadoption.dto.StaffDto(" +
            "s.id, s.name, s.address, s.shelter.id, s.user.id) " +
            "FROM Staff s " +
            "WHERE s.user.email = :email")
    StaffDto findStaffByUserEmail(@Param("email") String email);
}
