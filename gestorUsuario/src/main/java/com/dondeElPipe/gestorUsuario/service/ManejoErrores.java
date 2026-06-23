package com.dondeElPipe.gestorUsuario.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorUsuario.DTO.ErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

//@RestControllerAdvice
public class ManejoErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ResponseEntity<ErrorDTO> manejarErroresValidacion(
        MethodArgumentNotValidException ex,     //tiene el detalle de los errores de validacion
        HttpServletRequest request              //permite obtener informacion del request
    ) {
        //mapa para almacenar los errores por campo
        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        //se crea el objeto con la info del error
        ErrorDTO errorDTO = new ErrorDTO(
            LocalDateTime.now(),
            400,
            "Error de validación",
            errores,
            request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorDTO);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(
            Exception ex, HttpServletRequest request) {
        
        Map<String, Object> respuesta = new HashMap<>();
        
        respuesta.put("timestamp", LocalDateTime.now().toString());
        respuesta.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        respuesta.put("error", "Error Interno del Servidor");
        respuesta.put("message", ex.getMessage());
        respuesta.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
    }


}
