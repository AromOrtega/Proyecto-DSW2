package com.saludvital;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.saludvital.enums.EstadoCita;

@Entity
@Table(name = "citas")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(nullable = false)
    private LocalTime hora;

    @NotNull
    @Column(name = "paciente_id", nullable = false)
    private Long pacienteId;

    @NotNull
    @Column(name = "medico_id", nullable = false)
    private Long medicoId;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private EstadoCita estado = EstadoCita.PROGRAMADA;

    @Column(length = 500)
    private String motivo;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(precision = 10, scale = 2)
    private BigDecimal tarifaAplicada;

    public Cita() {}

    public Cita(LocalDate fecha,
                LocalTime hora,
                Long pacienteId,
                Long medicoId,
                String motivo,
                BigDecimal tarifaAplicada) {
        this.fecha = fecha;
        this.hora = hora;
        this.pacienteId = pacienteId;
        this.medicoId = medicoId;
        this.motivo = motivo;
        this.tarifaAplicada = tarifaAplicada;
        this.estado = EstadoCita.PROGRAMADA;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public Long getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(Long medicoId) {
        this.medicoId = medicoId;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        this.estado = estado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getTarifaAplicada() {
        return tarifaAplicada;
    }

    public void setTarifaAplicada(BigDecimal tarifaAplicada) {
        this.tarifaAplicada = tarifaAplicada;
    }
}
