package com.dondeElPipe.gestorCocina.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrdenCocinaDTO {

    private Integer id;
    private Integer pedidoId;
    private String nombreEstadoCocina; // "EN_ESPERA", "PREPARANDO", "LISTO"
    private LocalDateTime fechaIngreso;

}
