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
    
    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Metodos Crud--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    //buscar todo
    @GetMapping("/todo")
    public ResponseEntity<List<Inventario>> listar() {
        List<Inventario> inventarios = service.listar();
        return ResponseEntity.ok(inventarios);
    }
    
    //--+----+----+----+--Crear un insumo--+----+----+----+----+--
    //crear un inventario Response
    @PostMapping("/nuevo-insumo")
    public ResponseEntity<?> nuevoInventario(@Valid @RequestBody Inventario inventario) {
        Inventario nuevo = service.crearInsumo(inventario);

        if (nuevo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Error: El nombre del inventario '" + inventario.getNombreInsumo() + "' ya existe.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
    
    //--+----+----+----+--Eliminar un insumo--+----+----+----+----+--
    //eliminar un inventario por id
    @DeleteMapping("/eliminar-insumo/{id}")
    public ResponseEntity<?> eliminarInventario(@PathVariable Integer id) {
        Optional<Inventario> inventario = service.buscarId(id);

        if (inventario.isPresent()) {
            service.eliminarInsumo(id);
            return ResponseEntity.status(HttpStatus.OK)
                                    .body("Inventario ID " + id + " eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("El inventario " + id + " no fue encontrado");
        }
    }

    //--+----+----+----+--Actualizar un insumo--+----+----+----+----+--
    //actualizar un inventario
    @PutMapping("/modificar-insumo/{id}")
    public ResponseEntity<?> actualizarInventario(@Valid @PathVariable Integer id, @RequestBody Inventario inventario) {
        Optional<Inventario> existente = service.buscarId(id);

        if (existente.isPresent()) {
            service.actualizarInsumo(id, inventario);
            return ResponseEntity.status(HttpStatus.OK)
                                    .body("Inventario modificado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("El inventario " + id + " no fue encontrado");
        }
    }

}
