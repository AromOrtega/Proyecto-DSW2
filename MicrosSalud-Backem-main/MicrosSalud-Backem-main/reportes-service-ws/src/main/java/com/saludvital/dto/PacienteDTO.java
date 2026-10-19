package com.saludvital.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PacienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
