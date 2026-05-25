package com.dondeElPipe.gestorPedidos.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlatoMenuDTO {

    private Integer id;
    private String nombrePlato;
    private Double precio;

}
