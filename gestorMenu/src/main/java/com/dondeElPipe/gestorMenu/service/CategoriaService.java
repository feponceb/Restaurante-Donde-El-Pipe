package com.dondeElPipe.gestorMenu.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorMenu.model.CategoriaMenu;
import com.dondeElPipe.gestorMenu.repository.CategoriaMenuRepository;

@Service
public class CategoriaService {

    private final CategoriaMenuRepository repo;

    public CategoriaService(CategoriaMenuRepository repo) {
        this.repo = repo;
    }

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Crud básico--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    // 1. CREAR CATEGORÍA
    public CategoriaMenu crear(CategoriaMenu categoria) {
        // Limpiamos espacios extras
        String nombreLimpio = categoria.getNombre().trim().replaceAll("\\s+", " ");
        
        // Evitamos nombres duplicados
        if (repo.existsByNombreIgnoreCase(nombreLimpio)) {
            return null; 
        }
        
        categoria.setNombre(nombreLimpio);
        return repo.save(categoria);
    }

    // 2. LEER TODAS
    public List<CategoriaMenu> listarTodo() {
        return repo.findAll();
    }

    // 3. LEER POR ID
    public Optional<CategoriaMenu> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    // 4. ELIMINAR
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }

}
