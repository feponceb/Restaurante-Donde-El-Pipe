package com.dondeElPipe.gestorPedidos.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorPedidos.DTO.ErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

//@RestControllerAdvice
public class ManejadorErrores {

    // 1. Captura errores de validación de los campos (@Valid, @NotNull, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> errorValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Toma el primer mensaje de error que encuentre configurado en el modelo
        String mensaje = ex.getBindingResult().getFieldError().getDefaultMessage();
        
        ErrorDTO error = new ErrorDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Datos de entrada inválidos",
            mensaje,
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    // 2. Captura errores de lógica del negocio (las validaciones del Service con 'throw new IllegalArgumentException')
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> errorNegocio(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorDTO error = new ErrorDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Error de Regla de Negocio",
            ex.getMessage(), // Aquí viaja el mensaje del Service (ej: "No existe el pedido")
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

}
