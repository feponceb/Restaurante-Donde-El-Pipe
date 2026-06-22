package com.dondeElPipe.gestorPedidos.DTO;

import java.util.List;

import lombok.Data;

@Data
public class PedidoCrearDTO {
    private Integer idMesa;
    private Integer idGarzon;
    private List<Integer> platillosIds;
}
