package com.saludvital.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saludvital.Cita;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
	
}
