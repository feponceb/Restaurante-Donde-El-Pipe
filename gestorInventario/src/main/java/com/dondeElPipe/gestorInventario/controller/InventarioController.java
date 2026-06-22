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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorInventario.model.Inventario;
import com.dondeElPipe.gestorInventario.service.InventarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    //inyeccion de service
    @Autowired
    private InventarioService service;
    
    /**
     * Ver el estado actual de toda la bodega
     * GET: http://localhost:8081/inventario/listar
     */
    @GetMapping("/listar")
    public ResponseEntity<List<Inventario>> listarTodo() {
        return ResponseEntity.ok(service.obtenerTodo());
    }

    /**
     * AGREGAR / REABASTECER PRODUCTO
     * POST: http://localhost:8081/inventario/agregar
     * Body JSON: { "nombreIngrediente": "Pan", "stock": 50 }
     * Nota: Si "Pan" ya existía con 200, pasará a tener 250 de forma automática.
     */
    @PostMapping("/agregar")
    public ResponseEntity<Inventario> agregarOReabastecer(@Valid @RequestBody Inventario item) {
        Inventario guardado = service.agregarOReabastecer(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /**
     * MODIFICAR DATOS DE UN PRODUCTO POR ID
     * PUT: http://localhost:8081/inventario/modificar/3
     * Body JSON: { "nombreIngrediente": "Palta Hass", "stock": 15 }
     */
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id, @Valid @RequestBody Inventario item) {
        try {
            Inventario actualizado = service.modificarProducto(id, item);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Endpoint interno para que Cocina rebaje insumos.
     * PUT: http://localhost:8081/inventario/descontar
     */
    @PutMapping("/descontar")
    public ResponseEntity<String> descontar(@RequestBody List<String> ingredientes) {
        try {
            service.descontarStock(ingredientes);
            return ResponseEntity.ok("Stock actualizado correctamente en bodega.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
