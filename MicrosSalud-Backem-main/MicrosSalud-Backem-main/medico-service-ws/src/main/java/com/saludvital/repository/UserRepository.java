package com.saludvital.repository;

import com.saludvital.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de usuarios para medico-service.
 * Accede a la misma tabla 'users' que auth-service (misma BD).
 * Solo necesitamos las validaciones de unicidad para el registro de médicos.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);
}
