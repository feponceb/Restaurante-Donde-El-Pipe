package com.dondeElPipe.gestorCocina.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrdenCocinaDTO {

    private Integer id;
    private Integer pedidoId;
    private String estadoCocina;
    private LocalDateTime fechaIngreso;

}
