package com.saludvital.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.saludvital.Cita;
import com.saludvital.client.MedicoHttpClient;
import com.saludvital.dto.CitaDTO;
import com.saludvital.dto.CitaRequest;
import com.saludvital.enums.EstadoCita;
import com.saludvital.messaging.CitaEventPublisher;
import com.saludvital.respository.CitaRepository;

import jakarta.transaction.Transactional;

@Service
public class CitaService {

    private static final Logger log = LoggerFactory.getLogger(CitaService.class);

    private final CitaRepository citaRepository;
    private final CitaEventPublisher publisher;
    private final MedicoHttpClient medicoHttpClient;

    public CitaService(CitaRepository citaRepository,
            CitaEventPublisher publisher,
            MedicoHttpClient medicoHttpClient) {
        this.citaRepository = citaRepository;
        this.publisher = publisher;
        this.medicoHttpClient = medicoHttpClient;
    }

    public List<CitaDTO> findAll() {
        return citaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public CitaDTO findById(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        return toDTO(cita);
    }

    @Transactional
    public CitaDTO create(CitaRequest request) {
        Cita cita = new Cita();
        cita.setFecha(request.fecha());
        cita.setHora(request.hora());
        cita.setMotivo(request.motivo());
        cita.setEstado(EstadoCita.PROGRAMADA);
        cita.setPacienteId(request.pacienteId());
        cita.setMedicoId(request.medicoId());

        Cita saved = citaRepository.save(cita);
        publisher.publicarCitaCreada(saved.getId(), saved.getPacienteId(), saved.getMedicoId());
        return toDTO(saved);
    }

    @Transactional
    public CitaDTO update(Long id, CitaRequest request) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
        cita.setFecha(request.fecha());
        cita.setHora(request.hora());
        cita.setMotivo(request.motivo());
        return toDTO(citaRepository.save(cita));
    }

    @Transactional
    public void delete(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new RuntimeException("Cita no encontrada");
        }
        citaRepository.deleteById(id);
    }

    /**
     * Convierte la entidad Cita a DTO, resolviendo nombre del médico via
     * HttpExchange.
     */
    private CitaDTO toDTO(Cita c) {
        String nombreMedico = null;
        if (c.getMedicoId() != null) {
            try {
                Map<String, Object> medico = medicoHttpClient.obtenerMedicoPorId(c.getMedicoId());
                String nombre = (String) medico.get("nombre");
                String apellido = (String) medico.get("apellido");
                nombreMedico = (nombre != null ? nombre : "") + " " + (apellido != null ? apellido : "");
                nombreMedico = nombreMedico.trim();
            } catch (Exception e) {
                log.warn("No se pudo obtener médico id={}: {}", c.getMedicoId(), e.getMessage());
            }
        }
        return new CitaDTO(
                c.getId(),
                c.getFecha(),
                c.getHora(),
                c.getPacienteId(),
                c.getMedicoId(),
                nombreMedico,
                c.getMotivo(),
                c.getEstado());
    }
}