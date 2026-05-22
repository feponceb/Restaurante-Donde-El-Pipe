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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorMenu.DTO.MenuDTO;
import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.service.MenuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/menu/platillos")
public class MenuController {

    //inyeccion de service
    @Autowired
    private MenuService service;
    
    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Metodos Crud--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--
    //buscar todo
    @GetMapping("/todo")
    public ResponseEntity<List<Menu>> listar() {
        List<Menu> platos = service.listar();
        return ResponseEntity.ok(platos); // Equivale a status(HttpStatus.OK).body(platos)
    }

    // buscar todo en formato DTO
    @GetMapping("/todoDTO")
    public ResponseEntity<List<MenuDTO>> listarDTO() {
        // El service internamente ya se encarga de transformar las entidades a DTOs
        List<MenuDTO> menu = service.listarDTO();
        return ResponseEntity.ok(menu);
    }
    
    
    //crear un plato Response
    //DETALLE
    //no crear mismos nombres de platos
    @PostMapping("/nuevo-plato")
    public ResponseEntity<?> nuevoPlato(@Valid @RequestBody Menu menu){

        Menu nuevo = service.crearPlato(menu);

        // Si el servicio detectó un duplicado y devolvió null
        if (nuevo == null) {
            // Respondemos con código 400 Bad Request y un mensaje de error claro
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Error: El nombre del plato '" + menu.getNombrePlato() + "' ya existe.");
        }

        // Si se creó correctamente, respondemos con código 201 Created y el objeto completo (con su ID)
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
    

    //eliminar un plato por id
    @DeleteMapping("/eliminar-plato/{id}")
    public ResponseEntity<?> eliminarPlato(@PathVariable Integer id) {
        Optional<Menu> menu = service.buscarId(id);

        if (menu.isPresent()) {
            service.eliminarPlato(id);
            //codigo 200
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();                                   
        } else {
            //Codigo error 404
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("El plato " + id + " no fue encontrado");
        }
    }
    
    //actualizar un plato
    @PutMapping("/modificar-plato/{id}")
    public ResponseEntity<?> actualizarPlato(@Valid @PathVariable Integer id, @RequestBody Menu menu){
        Optional<Menu> existente = service.buscarId(id);

        if (existente.isPresent()) {
            service.actualizarMenu(id, menu);
            return ResponseEntity.status(HttpStatus.OK)
                                    .body("Plato modificado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("El plato " +id+ " no fue encontrado");
        }
    }

}
//Versiones Antiguas
/*
    //crear un plato
    @PostMapping("/nuevo-plato")
    public Menu nuevoPlato(@Valid @RequestBody Menu menu){
        return service.crearPlato(menu);
    }
    */
