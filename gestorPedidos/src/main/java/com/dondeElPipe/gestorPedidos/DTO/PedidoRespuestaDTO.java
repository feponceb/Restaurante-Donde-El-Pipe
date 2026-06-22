package com.dondeElPipe.gestorPedidos.DTO;

import java.util.List;

import lombok.Data;

@Data
public class PedidoRespuestaDTO {
    private Integer id;
    private Integer idMesa;
    private Integer idGarzon;
    private String estadoPedido;
    private Double totalPagar;
    private List<String> nombresPlatillos;

}
