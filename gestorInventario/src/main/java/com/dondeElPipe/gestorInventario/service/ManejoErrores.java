package com.dondeElPipe.gestorInventario.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorInventario.DTO.ErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ManejoErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> manejarErroresValidacion(
        MethodArgumentNotValidException ex,     // Tiene el detalle de los errores de validación
        HttpServletRequest request              // Permite obtener información del request actual
    ) {
        // Mapa para almacenar los errores dinámicos por cada campo del modelo Inventario
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        // Se encapsula la información utilizando el estándar unificado de tu DTO
        ErrorDTO errorDTO = new ErrorDTO(
            LocalDateTime.now(),
            400,
            "Error de validación en Inventario",
            errores,
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorDTO);
    }

}
