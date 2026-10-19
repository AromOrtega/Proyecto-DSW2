package com.saludvital.repository;

import com.saludvital.Role;
import com.saludvital.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> { 
    Optional<Role> findByName(ERole name);
}