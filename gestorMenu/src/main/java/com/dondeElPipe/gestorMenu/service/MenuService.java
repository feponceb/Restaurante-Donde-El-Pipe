package com.dondeElPipe.gestorMenu.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorMenu.DTO.MenuDTO;
import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.repository.CategoriaMenuRepository;
import com.dondeElPipe.gestorMenu.repository.MenuRepository;

@Service
public class MenuService {

    //inyeción del repository
    private final MenuRepository repo;
    private final CategoriaMenuRepository catRepo;

    public MenuService(MenuRepository repo, CategoriaMenuRepository catRepo){
        this.repo = repo;
        this.catRepo = catRepo;
    }

    

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Crud básico--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    //ver todos los platos
    public List<Menu> listar(){
        return repo.findAll();
    }
    
    //crear un plato
    public Menu crearPlato(Menu menu) {
        // 1. Validar que la categoría seleccionada por ID exista
        if (menu.getCategoria() == null || menu.getCategoria().getId() == null) {
            return null; 
        }

        // Verificamos en el repositorio de categorías si el ID realmente existe
        if (!catRepo.existsById(menu.getCategoria().getId())) {
            return null;
        }

        // 2. Limpiar espacios intermedios y validar duplicado de nombre del plato
        String nombreLimpio = menu.getNombrePlato().trim().replaceAll("\\s+", " ");
        if (repo.existsByNombrePlatoIgnoreCase(nombreLimpio)) {
            return null; // El controlador se encargará de responder con el código 400 Bad Request
        }

        Menu nuevoPlato = new Menu();
        nuevoPlato.setNombrePlato(nombreLimpio);
        nuevoPlato.setPrecio(menu.getPrecio());
        nuevoPlato.setCategoria(menu.getCategoria());

        return repo.save(nuevoPlato);
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
    
    public List<MenuDTO> listarTodoElMenu() {
        List<Menu> platos = repo.findAll();
        List<MenuDTO> listaDto = new ArrayList<>();
        
        for (Menu m : platos) {
            listaDto.add(convertirADto(m));
        }
        return listaDto;
    }

    public Optional<MenuDTO> buscarPorId(Integer id) {
        return repo.findById(id).map(this::convertirADto);
    }

    private MenuDTO convertirADto(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setNombrePlato(menu.getNombrePlato());
        dto.setPrecio(menu.getPrecio());
        // Evitamos NullPointerException si por alguna razón el plato no tiene categoría asignada
        if (menu.getCategoria() != null) {
            dto.setNombreCategoria(menu.getCategoria().getNombre());
        }
        return dto;
    }

}
