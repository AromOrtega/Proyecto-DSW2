package com.saludvital.service;

import com.saludvital.Medicamento;
import com.saludvital.dto.MedicamentoDTO;
import com.saludvital.messaging.MedicamentoEventPublisher;
import com.saludvital.repository.MedicamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicamentoService {

    private final MedicamentoRepository repo;
    private final MedicamentoEventPublisher publisher;

    public MedicamentoService(MedicamentoRepository repo, MedicamentoEventPublisher publisher) {
        this.repo = repo;
        this.publisher = publisher;
    }

    public List<Medicamento> findAll() {
        return repo.findAll();
    }

    public Medicamento findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado: " + id));
    }

    @Transactional
    public Medicamento create(MedicamentoDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (repo.existsByNombreIgnoreCase(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un medicamento con ese nombre");
        }
        Medicamento m = new Medicamento();
        m.setNombre(dto.getNombre().trim());
        m.setDescripcion(dto.getDescripcion());
        Medicamento saved = repo.save(m);
        publisher.publicarEvento("CREADO", saved.getId());
        return saved;
    }

    @Transactional
    public Medicamento update(Long id, MedicamentoDTO dto) {
        Medicamento m = findById(id);
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        String nuevoNombre = dto.getNombre().trim();
        if (!m.getNombre().equalsIgnoreCase(nuevoNombre) && repo.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new IllegalArgumentException("Ya existe un medicamento con ese nombre");
        }
        m.setNombre(nuevoNombre);
        m.setDescripcion(dto.getDescripcion());
        Medicamento saved = repo.save(m);
        publisher.publicarEvento("ACTUALIZADO", saved.getId());
        return saved;
    }

    @Transactional
    public void deleteById(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Medicamento no encontrado: " + id);
        }
        repo.deleteById(id);
        publisher.publicarEvento("ELIMINADO", id);
    }
}