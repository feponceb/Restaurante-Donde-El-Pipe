package com.dondeElPipe.gestorCocina.DTO;

import java.util.List;

import lombok.Data;

@Data
public class MenuExternoDTO {
    private Integer id;
    private String nombre;
    private Double precio;
    private List<String> ingredientes;

}
