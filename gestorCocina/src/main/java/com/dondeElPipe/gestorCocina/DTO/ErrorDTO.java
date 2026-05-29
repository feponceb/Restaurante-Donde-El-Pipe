package com.dondeElPipe.gestorCocina.DTO;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDTO {

    private LocalDateTime timestamp;
    private Integer status;
    private String mensaje;
    private Map<String, String> errores; // Campos específicos que fallaron
    private String path;

}
