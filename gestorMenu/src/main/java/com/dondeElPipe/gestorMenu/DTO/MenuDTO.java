package com.dondeElPipe.gestorMenu.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuDTO {

    private String nombrePlato;
    private Double precio;
    private String nombreCategoria;
    private List<String> ingredientes;

}
