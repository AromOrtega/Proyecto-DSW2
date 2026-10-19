package com.saludvital.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MedicamentoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
}
