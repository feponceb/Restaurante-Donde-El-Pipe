package com.dondeElPipe.gestorReserva.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorReserva.DTO.ErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

//@RestControllerAdvice
public class ManejadorErrores {

    // 1. Captura errores de validación (@NotNull, @Min, @Max)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> errorValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
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

    // 2. Captura errores de negocio (Mesa no encontrada, etc.)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> errorNegocio(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorDTO error = new ErrorDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(), // Cambiado a 404 para búsquedas fallidas
                "Recurso no encontrado",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

}
