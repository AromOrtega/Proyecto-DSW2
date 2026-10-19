package com.saludvital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saludvital.Medicamento;

import java.util.Optional;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {
    
    boolean existsByNombreIgnoreCase(String nombre);
    
    Optional<Medicamento> findByNombreIgnoreCase(String nombre);
}