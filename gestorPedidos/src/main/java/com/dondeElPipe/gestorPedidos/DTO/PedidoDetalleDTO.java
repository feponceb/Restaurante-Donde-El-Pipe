package com.dondeElPipe.gestorPedidos.DTO;

import java.util.List;

import lombok.Data;

@Data
public class PedidoDetalleDTO {
    private Integer idPedido;
    private String estadoPedido;
    private Double totalPagar;
    
    // Aquí es donde se unirá la información de los otros micros
    private String nombreGarzon; 
    private String estadoMesaActual; 
    private List<String> nombresPlatillos;
}
