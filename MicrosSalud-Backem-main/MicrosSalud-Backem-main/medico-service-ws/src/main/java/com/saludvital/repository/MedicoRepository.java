package com.saludvital.repository; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saludvital.Medico;
import com.saludvital.enums.Especialidad;
import com.saludvital.enums.EstadoDoctor;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    List<Medico> findByEspecialidad(Especialidad especialidad);
    List<Medico> findByEstado(EstadoDoctor estado);
    List<Medico> findByDisponible(Boolean disponible);

    boolean existsByNumeroLicencia(String numeroLicencia);
    Optional<Medico> findByNumeroLicencia(String numeroLicencia);
    boolean existsByNumeroLicenciaAndIdNot(String numero, Long id);

    Optional<Medico> findByUserId(Long userId);
}