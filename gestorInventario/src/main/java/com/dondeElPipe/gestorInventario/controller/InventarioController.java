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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/inventario")
public class InventarioController {

    //inyeccion de service
    @Autowired
    private InventarioService service;
    
    /**
     * Ver el estado actual de toda la bodega
     */
    @Operation(
        summary = "Listar estado de la bodega",
        description = "Obtiene la lista completa con todos los insumos e ingredientes registrados y sus respectivos niveles de stock"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Listado de inventario obtenido con éxito"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<Inventario>> listarTodo() {
        return ResponseEntity.ok(service.obtenerTodo());
    }

    /**
     * AGREGAR / REABASTECER PRODUCTO
     */
    @Operation(
        summary = "Agregar o reabastecer producto",
        description = "Registra un nuevo ingrediente o incrementa de forma acumulativa el stock de un insumo existente en base a su nombre"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Insumo registrado o reabastecido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada incorrectos o inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/agregar")
    public ResponseEntity<Inventario> agregarOReabastecer(@Valid @RequestBody Inventario item) {
        Inventario guardado = service.agregarOReabastecer(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    /**
     * MODIFICAR DATOS DE UN PRODUCTO POR ID
     */
    @Operation(
        summary = "Modificar un producto por ID",
        description = "Actualiza las propiedades y sobrescribe el stock de un ingrediente específico utilizando su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto modificado correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró ningún ingrediente asociado al ID provisto"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
     */
    @Operation(
        summary = "Descontar stock de insumos (Interno)",
        description = "Endpoint síncrono diseñado para que el microservicio de Cocina rebaje las existencias de una lista de ingredientes tras la preparación de un pedido"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado de manera exitosa en la bodega"),
        @ApiResponse(responseCode = "400", description = "Fallo al procesar el descuento (Existencias insuficientes o ingredientes no válidos)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
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
