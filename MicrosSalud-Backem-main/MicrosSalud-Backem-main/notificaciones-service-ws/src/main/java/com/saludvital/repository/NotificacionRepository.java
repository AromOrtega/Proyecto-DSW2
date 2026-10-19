package com.saludvital.repository;

import com.saludvital.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findAllByOrderByFechaCreacionDesc();

    List<Notificacion> findByUserIdOrderByFechaCreacionDesc(Long userId);
}
