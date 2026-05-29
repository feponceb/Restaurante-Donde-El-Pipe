package com.dondeElPipe.gestorCocina.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorCocina.DTO.ErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ManejoErrores {

    // 1. CAPTURA DE PARÁMETROS FALTANTES EN LA URL (Lo que te pasó en Postman)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorDTO> manejarParametroFaltante(MissingServletRequestParameterException ex, HttpServletRequest req) {
        // Extraemos el mensaje amigable: "Required request parameter 'pedidoId' is not present"
        String mensajeDetallado = "Falta un parámetro obligatorio en la petición: " + ex.getParameterName();
        
        ErrorDTO dto = new ErrorDTO(
            LocalDateTime.now(), 
            HttpStatus.BAD_REQUEST.value(), // 400
            mensajeDetallado, 
            null, 
            req.getRequestURI()
        );
        return ResponseEntity.badRequest().body(dto);
    }

    // 2. CAPTURA DE VALIDACIONES EN EL BODY (Campos incorrectos con @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> manejarValidaciones(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
        
        ErrorDTO dto = new ErrorDTO(LocalDateTime.now(), 400, "Error de validación en los datos", errores, req.getRequestURI());
        return ResponseEntity.badRequest().body(dto);
    }

    // 3. CAPTURA DE ERRORES DE LÓGICA DE NEGOCIO (IDs no encontrados en BD)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> manejarNegocio(IllegalArgumentException ex, HttpServletRequest req) {
        ErrorDTO dto = new ErrorDTO(LocalDateTime.now(), 400, ex.getMessage(), null, req.getRequestURI());
        return ResponseEntity.badRequest().body(dto);
    }

}
