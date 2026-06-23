package com.dondeElPipe.gestorInventario.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorInventario.DTO.ErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

//@RestControllerAdvice
public class ManejoErrores {

    // 1. CAPTURA ERRORES DE VALIDACIÓN (@Valid en los campos del Model)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> manejarErroresValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        ErrorDTO errorDTO = new ErrorDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Error de validación en los datos del Inventario",
            errores,
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorDTO);
    }

    // 2. CAPTURA ERRORES DE NEGOCIO (Insumos/Categorías no encontrados, nombres repetidos)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> manejarErroresNegocio(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorDTO errorDTO = new ErrorDTO(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            null,
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorDTO);
    }

}
