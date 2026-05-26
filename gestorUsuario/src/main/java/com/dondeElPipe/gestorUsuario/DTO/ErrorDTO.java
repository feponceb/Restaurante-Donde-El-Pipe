package com.dondeElPipe.gestorUsuario.DTO;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorDTO {

    private LocalDateTime timestamp;        //hora que se generó el error
    private int status;                     //codigo de herror HTTP
    private String mensaje;                 //mensaje de error general
    private Map<String, String> errores;    //detalle del error obtenido del especificado del model
    private String path;                    //ruta del endpoint donde falló

    //constructor para crear el objeto con los datos del error
    public ErrorDTO(LocalDateTime timestamp, int status, String mensaje, Map<String, String> errores, String path){
        this.timestamp = timestamp;
        this.status = status;
        this.mensaje = mensaje;
        this.errores = errores;
        this.path = path;
    }

}
