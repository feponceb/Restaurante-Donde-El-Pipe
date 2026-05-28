package com.dondeElPipe.gestorUsuario.controller;

import java.util.List;
import java.util.Optional;

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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    //buscar todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listar() {
        List<Usuario> usuarios = service.listar();
        return ResponseEntity.ok(usuarios);
    }

    // Buscar todo en formato DTO
    @GetMapping("/todoDTO")
    public ResponseEntity<List<UsuarioDTO>> listarUsuarios() {
        // Llama a tu función especial que mezcla los datos con el nombre del rol
        List<UsuarioDTO> usuariosDTO = service.listarDTO();
        return ResponseEntity.ok(usuariosDTO);
    }

    //crear un usuario Response
    @PostMapping("/nuevo-usuario")
    public ResponseEntity<?> nuevoUsuario(@Valid @RequestBody Usuario usuario) {
        Usuario nuevo = service.crearUsuario(usuario);

        if (nuevo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: El RUT o Email ya existen, el RUT es matemáticamente inválido, o el ID de Rol no existe.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    //eliminar un usuario por id
    @DeleteMapping("/eliminar-usuario/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        Optional<Usuario> usuario = service.buscarId(id);

        if (usuario.isPresent()) {
            service.eliminarUsuario(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("El usuario " + id + " no fue encontrado");
        }
    }

    //actualizar un usuario
    @PutMapping("/modificar-usuario/{id}")
    public ResponseEntity<?> actualizarUsuario(@Valid @PathVariable Integer id, @RequestBody Usuario usuario) {
        Optional<Usuario> existente = service.buscarId(id);

        if (existente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El usuario " + id + " no fue encontrado");
        }

        
        Usuario modificado = service.actualizarUsuario(id, usuario);

        // Si el servicio bloqueó la edición por un RUT/Email duplicado de OTRA persona o RUT inválido
        if (modificado == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al actualizar: Verifique que el RUT sea válido y que el RUT o Email no pertenezcan a otro usuario.");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body("Usuario modificado correctamente");
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable Integer id) {
        Optional<Usuario> usuarioOpt = service.buscarId(id); // Usa el método de tu Service
        
        if (usuarioOpt.isPresent()) {
            return ResponseEntity.ok(usuarioOpt.get()); // Retorna el usuario completo (200 OK)
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Usuario con ID " + id + " no encontrado.");
        }
    }

}

