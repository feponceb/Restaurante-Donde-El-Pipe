package com.dondeElPipe.gestorMenu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.repository.MenuRepository;

@Service
public class MenuService {

    //inyeción del repository
    @Autowired
    private MenuRepository repo;

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Crud básico--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    //ver todos los platos
    public List<Menu> listar(){
        return repo.findAll();
    }
    //crear un plato
    public Menu crearPlato(Menu menu){
        return repo.save(menu);
    }

    //eliminar un plato por el id
    public void eliminarPlato(Integer id){
        repo.deleteById(id);
    }
    //modificar un plato
    public Menu actualizarMenu(Integer id, Menu menu){
        menu.setId(id);
        return repo.save(menu);
    }

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+--Funciones especiales--+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    //buscar por id
    public Optional<Menu> buscarId(Integer id){
        return repo.findById(id);
    }
    /* 
    //buscar por nombre
    public Optional<Menu> findByName(String name){
        return repo.findByNameIgnoreCase(name); 
    }
    */
}
