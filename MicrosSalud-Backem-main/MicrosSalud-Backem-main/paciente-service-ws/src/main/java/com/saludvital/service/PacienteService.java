package com.saludvital.service;

import com.saludvital.Paciente;
import com.saludvital.messaging.PacienteEventPublisher;
import com.saludvital.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final PacienteEventPublisher publisher;

    // ✅ Constructor injection (reemplaza @Autowired en campo)
    public PacienteService(PacienteRepository pacienteRepository, PacienteEventPublisher publisher) {
        this.pacienteRepository = pacienteRepository;
        this.publisher = publisher;
    }

    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> findById(Long id) {
        return pacienteRepository.findById(id);
    }

    public Optional<Paciente> findByUsuarioId(Long userId) {
        return pacienteRepository.findByUsuarioId(userId);
    }

    @Transactional
    public Paciente save(Paciente p) {
        boolean isNew = (p.getId() == null);
        Paciente saved = pacienteRepository.save(p);
        publisher.publicarEvento(isNew ? "CREADO" : "ACTUALIZADO", saved.getId());
        return saved;
    }

    @Transactional
    public void delete(Long id) {
        pacienteRepository.deleteById(id);
        publisher.publicarEvento("ELIMINADO", id);
    }
    
    
    
    



}