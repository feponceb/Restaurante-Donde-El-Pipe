package com.dondeElPipe.gestorMenu.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.service.MenuService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/menu")
public class MenuController {

    //inyeccion de service
    @Autowired
    private MenuService service;
    
    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Metodos Crud--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--
    //buscar todo
    @GetMapping("/todo")
    public List<Menu> listar(){
        return service.listar();
    }
    //crear un plato
    @PostMapping("/nuevo-plato")
    public Menu nuevoPlato(@Valid @RequestBody Menu menu){
        return service.crearPlato(menu);
    }
    //eliminar un plato por id
    @DeleteMapping("/eliminar-plato/{id}")
    public String eliminarPlato(@PathVariable Integer id){
        Optional<Menu> menu = service.buscarId(id);

        if (menu.isPresent()) {
            service.eliminarPlato(id);
            return "Plato eliminado correctamente";
        } else {
            return "El plato " +id+ " no fue encontrado";
        }
    }
    //actualizar un plato
    @PutMapping("/modificar-plato/{id}")
    public String actualizarPlato(@PathVariable Integer id, @RequestBody Menu menu){
        Optional<Menu> existente = service.buscarId(id);

        if (existente.isPresent()) {
            service.actualizarMenu(id, menu);
            return "Plato modificado correctamente";
        } else {
            return "El plato " +id+ " no fue encontrado";
        }
    }

}
