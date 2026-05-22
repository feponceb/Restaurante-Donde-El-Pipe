package com.dondeElPipe.gestorInventario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorInventario.model.CategoriaInsumo;
import com.dondeElPipe.gestorInventario.repository.CategoriaInsumoRepository;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaInsumoRepository repo;

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Crud básico--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    // 1. CREAR CATEGORÍA
    public CategoriaInsumo crear(CategoriaInsumo categoria) {
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
    public List<CategoriaInsumo> listarTodo() {
        return repo.findAll();
    }

    // 3. LEER POR ID
    public Optional<CategoriaInsumo> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    // 4. ELIMINAR
    public void eliminar(Integer id) {
        repo.deleteById(id);
    }
}
