package com.dondeElPipe.gestorInventario.controller;

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

import com.dondeElPipe.gestorInventario.model.CategoriaInsumo;
import com.dondeElPipe.gestorInventario.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventario/categoria")
public class CategoriaController {

    @Autowired
    private CategoriaService service;

    // Endpoint: Listar todas
    @GetMapping("/todo")
    public ResponseEntity<List<CategoriaInsumo>> obtenerCategorias() {
        return ResponseEntity.ok(service.listarTodo());
    }

    // Endpoint: Crear nueva categoría
    @PostMapping("/nueva")
    public ResponseEntity<?> crearCategoria(@Valid @RequestBody CategoriaInsumo categoria) {
        CategoriaInsumo nueva = service.crear(categoria);
        if (nueva == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: La categoría '" + categoria.getNombre() + "' ya existe.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    // Endpoint: Eliminar categoría (Forzando 204 No Content)
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        Optional<CategoriaInsumo> encontrada = service.buscarPorId(id);
        
        if (encontrada.isPresent()) {
            service.eliminar(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // 204 Exitoso vacío
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La categoría con ID " + id + " no existe.");
        }
    }

}
