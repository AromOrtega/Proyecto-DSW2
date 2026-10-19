package com.saludvital;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.saludvital.enums.Especialidad;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.saludvital.enums.EstadoDoctor;

@Entity
@Table(name = "medicos")
public class Medico {
	

	    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String nombre;
	    private String apellido;

	    @Column(unique = true)
	    private String numeroLicencia;

	    private String telefono;
	    private String email;

	    @Enumerated(EnumType.STRING)
	    private Especialidad especialidad;

	    @Enumerated(EnumType.STRING)
	    private EstadoDoctor estado;

	    private Boolean disponible;
	    private BigDecimal tarifaConsulta;

	    @Column(name = "user_id")
	    private Long userId;

	    @OneToMany(mappedBy = "medico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	    @JsonManagedReference
	    private List<HorarioAtencion> horarios = new java.util.ArrayList<>();

	    public void addHorario(HorarioAtencion h) {
	        horarios.add(h);
	        h.setMedico(this);
	    }
	    public void removeHorario(HorarioAtencion h) {
	        horarios.remove(h);
	        h.setMedico(null);
	    }

	    // --- Getters y Setters ---
	    public Long getId() { return id; }
	    public void setId(Long id) { this.id = id; }
	    public String getNombre() { return nombre; }
	    public void setNombre(String nombre) { this.nombre = nombre; }
	    public String getApellido() { return apellido; }
	    public void setApellido(String apellido) { this.apellido = apellido; }
	    public String getNumeroLicencia() { return numeroLicencia; }
	    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }
	    public String getTelefono() { return telefono; }
	    public void setTelefono(String telefono) { this.telefono = telefono; }
	    public String getEmail() { return email; }
	    public void setEmail(String email) { this.email = email; }
	    public Especialidad getEspecialidad() { return especialidad; }
	    public void setEspecialidad(Especialidad especialidad) { this.especialidad = especialidad; }
	    public EstadoDoctor getEstado() { return estado; }
	    public void setEstado(EstadoDoctor estado) { this.estado = estado; }
	    public Boolean getDisponible() { return disponible; }
	    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
	    public BigDecimal getTarifaConsulta() { return tarifaConsulta; }
	    public void setTarifaConsulta(BigDecimal tarifaConsulta) { this.tarifaConsulta = tarifaConsulta; }
	    
	    public Long getUserId() { return userId; }
	    public void setUserId(Long userId) { this.userId = userId; }
	    
	    public List<HorarioAtencion> getHorarios() { return horarios; }
	    public void setHorarios(List<HorarioAtencion> horarios) { this.horarios = horarios; }
	

}
