package com.dondeElPipe.gestorPagos.DTO;


import lombok.Data;

@Data
public class PedidoSimpleDTO {

    private Integer id;
    private String estadoPedido; 
    private Integer idMesa; // Importante para actualizar la mesa después
    private Double totalPagar;

}
