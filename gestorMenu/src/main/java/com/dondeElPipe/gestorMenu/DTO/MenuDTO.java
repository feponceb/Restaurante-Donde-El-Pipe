package com.dondeElPipe.gestorMenu.DTO;

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

}
