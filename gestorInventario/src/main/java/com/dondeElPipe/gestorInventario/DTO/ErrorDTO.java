package com.dondeElPipe.gestorInventario.DTO;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorDTO {

    private LocalDateTime timestamp;        // Hora en que se generó el error
    private int status;                     // Código de error HTTP
    private String mensaje;                 // Mensaje de error general
    private Map<String, String> errores;    // Detalle obtenido de las anotaciones del Model
    private String path;                    // Ruta del endpoint donde falló

    // Constructor explícito para instanciar el DTO de errores de forma controlada
    public ErrorDTO(LocalDateTime timestamp, int status, String mensaje, Map<String, String> errores, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.mensaje = mensaje;
        this.errores = errores;
        this.path = path;
    }

}
