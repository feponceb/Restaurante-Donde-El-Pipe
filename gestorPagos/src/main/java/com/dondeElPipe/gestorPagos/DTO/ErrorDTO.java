package com.dondeElPipe.gestorPagos.DTO;

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
    private Map<String, String> errores; // Almacena el campo fallido y su mensaje
    private String path;

}
