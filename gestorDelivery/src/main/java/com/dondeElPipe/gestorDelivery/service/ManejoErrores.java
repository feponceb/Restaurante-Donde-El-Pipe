package com.dondeElPipe.gestorDelivery.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorDelivery.DTO.ErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ManejoErrores {

    // 1. CAPTURA DE ERRORES DE LÓGICA DE NEGOCIO (IDs no encontrados, datos obligatorios vacíos)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> manejarNegocio(IllegalArgumentException ex, HttpServletRequest req) {
        ErrorDTO dto = new ErrorDTO(
            LocalDateTime.now(), 
            HttpStatus.BAD_REQUEST.value(), // 400
            ex.getMessage(), 
            null, 
            req.getRequestURI()
        );
        return ResponseEntity.badRequest().body(dto);
    }

    // 2. CAPTURA DE VALIDACIONES EN EL BODY (Por si usas @Valid en el modelo)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> manejarValidaciones(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
        
        ErrorDTO dto = new ErrorDTO(
            LocalDateTime.now(), 
            HttpStatus.BAD_REQUEST.value(), 
            "Error de validación en los datos de entrada", 
            errores, 
            req.getRequestURI()
        );
        return ResponseEntity.badRequest().body(dto);
    }

    // 3. CAPTURA GENÉRICA DE CUALQUIER OTRO ERROR IMPREVISTO
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> manejarCualquierError(Exception ex, HttpServletRequest req) {
        ErrorDTO dto = new ErrorDTO(
            LocalDateTime.now(), 
            HttpStatus.INTERNAL_SERVER_ERROR.value(), // 500
            "Ocurrió un error inesperado en el servidor: " + ex.getMessage(), 
            null, 
            req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(dto);
    }

}
