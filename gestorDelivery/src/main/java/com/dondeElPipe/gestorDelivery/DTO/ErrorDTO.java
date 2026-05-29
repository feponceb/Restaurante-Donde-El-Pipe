package com.dondeElPipe.gestorDelivery.DTO;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDTO {

    private LocalDateTime timestamp;
    private Integer status;
    private String mensaje;
    private Map<String, String> errores; 
    private String path;

}
