package com.saludvital.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MedicoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private String numeroLicencia;
    private String email;
    private String telefono;
    private String estado;
}
