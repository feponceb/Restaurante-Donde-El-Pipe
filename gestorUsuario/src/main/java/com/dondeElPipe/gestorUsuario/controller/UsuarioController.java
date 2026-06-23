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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorUsuario.DTO.UsuarioDTO;
import com.dondeElPipe.gestorUsuario.model.Usuario;
import com.dondeElPipe.gestorUsuario.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    // GET: listar todos los usuarios
    @Operation(
        summary = "Listar usuarios",
        description = "Obtiene una lista con todos los usuarios registrados en el sistema en formato DTO"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<UsuarioDTO>> listar() {
        List<UsuarioDTO> usuarios = service.listar();
        return ResponseEntity.ok(usuarios);
    }

    // POST: crear un usuario Response
    @Operation(
        summary = "Crear usuario",
        description = "Registra un nuevo usuario en el sistema validando las reglas de negocio"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos (RUT o Email ya existen, o formato incorrecto)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/nuevo-usuario")
    public ResponseEntity<?> nuevoUsuario(@Valid @RequestBody Usuario usuario) {
        UsuarioDTO nuevo = service.crearUsuario(usuario);

        if (nuevo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El RUT o Email ya existen, el RUT es matemáticamente inválido, o el ID de Rol no existe.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // DELETE: eliminar un usuario por id
    @Operation(
        summary = "Eliminar usuario",
        description = "Remueve un usuario del sistema mediante su identificador único ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/eliminar-usuario/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        Optional<UsuarioDTO> usuario = service.buscarId(id);

        if (usuario.isPresent()) {
            service.eliminarUsuario(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("El usuario " + id + " no fue encontrado");
        }
    }

    // PUT: actualizar un usuario
    @Operation(
        summary = "Actualizar usuario",
        description = "Modifica los datos de un usuario existente utilizando su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o error en la actualización"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/modificar-usuario/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @Valid @RequestBody Usuario usuario) {
        Optional<UsuarioDTO> existente = service.buscarId(id);

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario " + id + " no fue encontrado");
        }

        Usuario modificado = service.actualizarUsuario(id, usuario);

        if (modificado == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar: Verifique que el RUT sea válido y que el RUT o Email no pertenezcan a otro usuario.");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body("Usuario modificado correctamente");
    }

    // GET: buscar por ID
    @Operation(
        summary = "Buscar usuario por ID",
        description = "Obtiene los datos de un usuario específico usando su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Integer id) {
        Optional<UsuarioDTO> usuarioOpt = service.buscarId(id); 
        
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get()); 
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario con ID " + id + " no encontrado.");
        }
    }

}

