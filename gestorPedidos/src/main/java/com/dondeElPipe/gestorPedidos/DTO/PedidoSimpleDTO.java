package com.dondeElPipe.gestorPedidos.DTO;

import java.util.List;

import lombok.Data;

@Data
public class PedidoSimpleDTO {

    private Integer idPedido;
    private Integer tipoPedidoId;   // El número (1)
    private String tipoPedido;      // El texto ("LOCAL") para mantener compatibilidad con Pagos
    private Integer estadoPedidoId; // El número (1)
    private String estadoPedido;    // El texto ("PENDIENTE")
    private Integer usuarioId;
    private Integer mesaId;
    private Double totalPedido;
    private List<DetallePedidoDTO> detalles;

}
