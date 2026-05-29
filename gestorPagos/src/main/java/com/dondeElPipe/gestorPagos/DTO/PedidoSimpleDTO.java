package com.dondeElPipe.gestorPagos.DTO;

import java.util.List;

import lombok.Data;

@Data
public class PedidoSimpleDTO {

    private Integer idPedido;
    private String tipoPedido;     // Aquí llegará "LOCAL", "RETIRO" o "DELIVERY"
    private String estadoPedido;   // Aquí llegará "PENDIENTE", "PAGADO", etc.
    private Integer usuarioId;
    private Integer mesaId;        // ID de la mesa si es LOCAL
    private Double totalPedido;
    private List<DetallePedidoDTO> detalles;

}
