package com.dondeElPipe.gestorDelivery.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DespachoDTO {

    private Integer id;
    private Integer pedidoId;
    private String direccionEntrega;
    private Integer repartidorId;
    private String estadoDelivery;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaEntrega;

}
