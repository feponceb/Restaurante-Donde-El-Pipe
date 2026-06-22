package com.dondeElPipe.gestorPagos.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagoDTO {

    private Integer id;
    private Integer pedidoId;
    private Double monto;
    private String metodoPago;
    private String estado;
    private LocalDateTime fechaPago;

}
