package com.dondeElPipe.gestorPedidos.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetallePedidoDTO {

    private String nombrePlato; 
    private Integer cantidad;
    private Double subtotal;

}
