package com.dondeElPipe.gestorPedidos.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

}
