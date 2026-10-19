package com.saludvital.dto;
import java.time.LocalDate;
import java.util.List;

public class PacienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String numeroIdentificacion;
    private LocalDate fechaNacimiento;
    private String telefono;
    private String direccion;
    private Long usuarioId;
    private List<OptionDTO> alergias;
    private List<Long> alergiaIds;
    

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String n) { this.nombre = n; }
    public String getApellido() { return apellido; }
    public void setApellido(String a) { this.apellido = a; }
    public String getNumeroIdentificacion() { return numeroIdentificacion; }
    public void setNumeroIdentificacion(String ni) { this.numeroIdentificacion = ni; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fn) { this.fechaNacimiento = fn; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String t) { this.telefono = t; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String d) { this.direccion = d; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long uid) { this.usuarioId = uid; }
    public List<OptionDTO> getAlergias() { return alergias; }
    public void setAlergias(List<OptionDTO> al) { this.alergias = al; }
    public List<Long> getAlergiaIds() { return alergiaIds; }
    public void setAlergiaIds(List<Long> ids) { this.alergiaIds = ids; }
}