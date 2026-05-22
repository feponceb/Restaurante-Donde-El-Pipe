package com.dondeElPipe.gestorInventario.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventarioDTO {

    private Integer id;
    private String nombreInsumo;
    private Double stockActual;
    private String unidadMedida;
    private String nombreCategoria;

}
