package com.dondeElPipe.gestorUsuario.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorUsuario.model.Rol;
import com.dondeElPipe.gestorUsuario.service.RolService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuario/roles")
public class RolController {

    @Autowired
    private RolService service;

    // Endpoint: Listar todos los roles existentes
    @GetMapping("/todo")
    public ResponseEntity<List<Rol>> obtenerRoles() {
        return ResponseEntity.ok(service.listarTodo());
    }

    // Endpoint: Crear un nuevo rol (ej: ADMIN, GARZON)
    @PostMapping("/nuevo")
    public ResponseEntity<?> crearRol(@Valid @RequestBody Rol rol) {
        Rol nuevo = service.crear(rol);
        if (nuevo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El rol '" + rol.getNombre() + "' ya existe o es inválido.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // Endpoint: Eliminar un rol por ID (Forzando código 204 No Content)
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarRol(@PathVariable Integer id) {
        Optional<Rol> encontrado = service.buscarPorId(id);
        
        if (encontrado.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 Exitoso sin cuerpo
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El rol con ID " + id + " no existe.");
        }
    }

}
