package com.dondeElPipe.gestorPedidos.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.dondeElPipe.gestorPedidos.model.EstadoPedido;
import com.dondeElPipe.gestorPedidos.model.TipoPedido;

import lombok.Data;

@Data
public class PedidoLegibleDTO {

    private Integer idPedido;
    private Integer numeroMesa;    // Si es null, el Chef sabe que es Delivery
    private TipoPedido tipoPedido;
    private Integer usuarioId;    
    private Double totalPagar;
    private EstadoPedido estado;
    private LocalDateTime fechaCreacion;
    private List<DetallePedidoDTO> platosPedidos;

}
