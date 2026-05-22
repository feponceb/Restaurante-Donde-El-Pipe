package com.dondeElPipe.gestorUsuario.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String nombreRol;

}
