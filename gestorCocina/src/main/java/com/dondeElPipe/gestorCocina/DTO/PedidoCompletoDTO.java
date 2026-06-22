package com.dondeElPipe.gestorCocina.DTO;

import java.util.List;

import lombok.Data;

@Data
public class PedidoCompletoDTO {
    private Integer id;
    private List<Integer> platillosIds;

}
