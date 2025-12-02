package com.petadoption.repository;

import com.petadoption.entity.User;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by username
    Optional<User> findByEmail(String username);

    boolean existsByEmail(String email);

}
