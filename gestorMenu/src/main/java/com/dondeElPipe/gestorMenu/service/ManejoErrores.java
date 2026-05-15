package com.dondeElPipe.gestorMenu.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dondeElPipe.gestorMenu.DTO.ErrorDTO;

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

}
