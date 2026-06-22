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

    private final MenuRepository repo;
    private final CategoriaMenuRepository catRepo;

    public MenuService(MenuRepository repo, CategoriaMenuRepository catRepo){
        this.repo = repo;
        this.catRepo = catRepo;
    }

    public List<Menu> listar(){
        return repo.findAll();
    }
    
    public Menu crearPlato(Menu menu) {
        if (menu.getCategoria() == null || menu.getCategoria().getId() == null) {
            return null; 
        }

        if (!catRepo.existsById(menu.getCategoria().getId())) {
            return null;
        }

        String nombreLimpio = menu.getNombrePlato().trim().replaceAll("\\s+", " ");
        if (repo.existsByNombrePlatoIgnoreCase(nombreLimpio)) {
            return null; 
        }

        Menu nuevoPlato = new Menu();
        nuevoPlato.setNombrePlato(nombreLimpio);
        nuevoPlato.setDescripcion(menu.getDescripcion()); // <- NUEVO
        nuevoPlato.setPrecio(menu.getPrecio());
        nuevoPlato.setCategoria(menu.getCategoria());
        nuevoPlato.setIngredientes(menu.getIngredientes()); // <- NUEVO

        return repo.save(nuevoPlato);
    }

    public void eliminarPlato(Integer id){
        repo.deleteById(id);
    }

    public Menu actualizarMenu(Integer id, Menu menu){
        menu.setId(id);
        return repo.save(menu);
    }

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
        dto.setId(menu.getId()); // <- NUEVO
        dto.setNombrePlato(menu.getNombrePlato());
        dto.setDescripcion(menu.getDescripcion()); // <- NUEVO
        dto.setPrecio(menu.getPrecio());
        dto.setIngredientes(menu.getIngredientes()); // <- NUEVO
        
        if (menu.getCategoria() != null) {
            dto.setNombreCategoria(menu.getCategoria().getNombre());
        }
        return dto;
    }

}
