package com.dondeElPipe.gestorMenu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorMenu.DTO.MenuDTO;
import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.repository.CategoriaMenuRepository;
import com.dondeElPipe.gestorMenu.repository.MenuRepository;

@Service
public class MenuService {

    //inyeción del repository
    @Autowired
    private MenuRepository repo;

    @Autowired
    private CategoriaMenuRepository catRepo;

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
    
    // Listar todo mapeado a DTO
    public List<MenuDTO> listarDTO() {
        List<Menu> platos = repo.findAll();

        return platos.stream().map(plato -> {
            String nombreCat = (plato.getCategoria() != null)
                                      ? plato.getCategoria().getNombre()
                                      :"SIN CATEGORIA";

            // 2. Extraer los nombres de la lista de ingredientes asociados al plato
            List<String> nombresIngredientes = (plato.getIngredientes() != null)
                ? plato.getIngredientes().stream()
                                     .map(ingrediente -> ingrediente.getNombre())
                                     .toList()
                : List.of(); // Lista vacía si no tiene ingredientes

            // 3. Retornar el DTO con la nueva estructura
            return new MenuDTO(
                plato.getNombrePlato(),
                plato.getPrecio(),
                nombreCat,
                nombresIngredientes
            );
        }).toList();
    }

}
