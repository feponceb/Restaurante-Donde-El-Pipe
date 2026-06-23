package com.dondeElPipe.gestorMenu.controller;

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

import com.dondeElPipe.gestorMenu.model.CategoriaMenu;
import com.dondeElPipe.gestorMenu.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/menu/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService service;

    // Endpoint: Listar todas
    @Operation(
        summary = "Listar todas las categorías",
        description = "Obtiene una lista de todas las clasificaciones de menú disponibles (Ej: Entradas, Fondos, Bebestibles)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/todo")
    public ResponseEntity<List<CategoriaMenu>> obtenerCategorias() {
        return ResponseEntity.ok(service.listarTodo());
    }

    // Endpoint: Crear nueva categoría
    @Operation(
        summary = "Crear nueva categoría",
        description = "Registra una clasificación de menú validando que no exista un nombre idéntico duplicado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Categoría creada con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (La categoría ya existe en el sistema)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/nueva")
    public ResponseEntity<?> crearCategoria(@Valid @RequestBody CategoriaMenu categoria) {
        CategoriaMenu nueva = service.crear(categoria);
        if (nueva == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: La categoría '" + categoria.getNombre() + "' ya existe.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // Endpoint: Eliminar categoría (Forzando 204 No Content)
    @Operation(
        summary = "Eliminar categoría por ID",
        description = "Remueve permanentemente una categoría de menú mediante su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoría eliminada con éxito (Sin cuerpo de respuesta)"),
        @ApiResponse(responseCode = "404", description = "La categoría con el ID especificado no existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        Optional<CategoriaMenu> encontrada = service.buscarPorId(id);
        
        if (encontrada.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 Exitoso vacío
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La categoría con ID " + id + " no existe.");
        }
    }

}
