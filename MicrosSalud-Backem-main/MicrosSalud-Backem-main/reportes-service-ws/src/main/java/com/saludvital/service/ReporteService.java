package com.saludvital.service;

import com.saludvital.client.CitaClient;
import com.saludvital.client.MedicamentoClient;
import com.saludvital.client.MedicoClient;
import com.saludvital.client.PacienteClient;
import com.saludvital.dto.CitaDTO;
import com.saludvital.dto.MedicamentoDTO;
import com.saludvital.dto.MedicoDTO;
import com.saludvital.dto.PacienteDTO;
import com.saludvital.messaging.ReporteEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteService {

    private final CitaClient citaClient;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;
    private final MedicamentoClient medicamentoClient;
    private final ReporteEventPublisher publisher;

    public ReporteService(CitaClient citaClient,
            MedicoClient medicoClient,
            PacienteClient pacienteClient,
            MedicamentoClient medicamentoClient,
            ReporteEventPublisher publisher) {
        this.citaClient = citaClient;
        this.medicoClient = medicoClient;
        this.pacienteClient = pacienteClient;
        this.medicamentoClient = medicamentoClient;
        this.publisher = publisher;
    }

    public List<CitaDTO> reporteCitas() {
        List<CitaDTO> citas = citaClient.listarCitas();
        publisher.publicarReporteGenerado("CITAS");
        return citas;
    }

    public List<MedicoDTO> reporteMedicos() {
        List<MedicoDTO> medicos = medicoClient.listarMedicos();
        publisher.publicarReporteGenerado("MEDICOS");
        return medicos;
    }

    public List<PacienteDTO> reportePacientes() {
        List<PacienteDTO> pacientes = pacienteClient.listarPacientes();
        publisher.publicarReporteGenerado("PACIENTES");
        return pacientes;
    }

    public List<MedicamentoDTO> reporteMedicamentos() {
        List<MedicamentoDTO> medicamentos = medicamentoClient.listarMedicamentos();
        publisher.publicarReporteGenerado("MEDICAMENTOS");
        return medicamentos;
    }
}
