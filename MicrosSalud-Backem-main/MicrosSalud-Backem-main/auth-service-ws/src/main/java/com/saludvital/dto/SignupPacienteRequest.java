package com.saludvital.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para registrar un nuevo paciente (auto-registro público y registro por
 * admin).
 * Contiene las credenciales del usuario y los datos del perfil del paciente.
 */
public class SignupPacienteRequest {

    // ── Credenciales ───────────────────────────────────────────────────────
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "El username debe tener entre 3 y 20 caracteres")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 40, message = "La contraseña debe tener entre 6 y 40 caracteres")
    private String password;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Size(max = 50)
    @Email(message = "El correo electrónico no tiene un formato válido")
    private String email;

    // ── Perfil del Paciente ────────────────────────────────────────────────
    private String nombre;
    private String apellido;
    private String numeroIdentificacion;

    /** Formato esperado: yyyy-MM-dd (ej: 2001-02-10) */
    private String fechaNacimiento;

    private String telefono;
    private String direccion;

    // ── Getters & Setters ──────────────────────────────────────────────────
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String numeroIdentificacion) {
        this.numeroIdentificacion = numeroIdentificacion;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
