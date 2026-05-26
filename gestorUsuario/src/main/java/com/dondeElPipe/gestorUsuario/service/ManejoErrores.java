package com.dondeElPipe.gestorUsuario.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorUsuario.DTO.ErrorDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> manejarErroresNegocio(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        // Construye el ErrorDTO extrayendo el mensaje exacto enviado desde el Service
        ErrorDTO errorDTO = new ErrorDTO(
                LocalDateTime.now(),
                400,            // Estado 400 Bad Request correcto para Postman
                ex.getMessage(), // Aquí viajará "El email ya está registrado" o "El RUT ya está registrado"
                null,           // Detalle en null según el formato de persistencia del PPT
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(errorDTO);
    }


}
