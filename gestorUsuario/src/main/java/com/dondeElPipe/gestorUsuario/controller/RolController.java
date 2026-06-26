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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios/roles")
public class RolController {

    @Autowired
    private RolService service;

    // GET: Listar todos los roles existentes
    @Operation(
        summary = "Listar roles",
        description = "Obtiene una lista con todos los roles registrados en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/todo")
    public ResponseEntity<List<Rol>> obtenerRoles() {
        return ResponseEntity.ok(service.listarTodo());
    }

    // POST: Crear un nuevo rol (ej: ADMIN, GARZON)
    @Operation(
        summary = "Crear nuevo rol",
        description = "Registra un nuevo rol de usuario validando que no existan duplicados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Rol creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos (El rol ya existe o es inválido)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/nuevo")
    public ResponseEntity<?> crearRol(@Valid @RequestBody Rol rol) {
        Rol nuevo = service.crear(rol);
        if (nuevo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El rol '" + rol.getNombre() + "' ya existe o es inválido.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // DELETE: Eliminar un rol por ID
    @Operation(
        summary = "Eliminar rol por ID",
        description = "Remueve un rol específico del sistema mediante su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Rol eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
