package com.saludvital.repository;

import com.saludvital.Role;
import com.saludvital.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio de roles para medico-service.
 * Accede a la misma tabla 'roles' que auth-service (misma BD).
 * Se usa para asignar automáticamente ROLE_MEDICO al crear el usuario.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
}
