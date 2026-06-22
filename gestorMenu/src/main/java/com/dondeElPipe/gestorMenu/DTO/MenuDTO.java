package com.dondeElPipe.gestorMenu.DTO;


import java.util.List;

import lombok.Data;

@Data
public class MenuDTO {

    private Integer id; // Es recomendable enviar el ID para que gestorPedidos lo reconozca
    private String nombrePlato;
    private String descripcion;
    private Double precio;
    private String nombreCategoria;
    private List<String> ingredientes;

}
