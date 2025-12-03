package com.petadoption.repository;

import com.petadoption.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShelterRepository extends JpaRepository<Shelter,Long> {
    @Query("SELECT DISTINCT s FROM Shelter s " +
            "LEFT JOIN FETCH s.contacts " +
            "LEFT JOIN FETCH s.workingHours " +
            "LEFT JOIN FETCH s.staffMembers st " +
            "LEFT JOIN FETCH st.user")
List<Shelter> findAllWithDetails();

}
