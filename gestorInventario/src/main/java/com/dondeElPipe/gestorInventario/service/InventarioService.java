package com.dondeElPipe.gestorInventario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorInventario.model.Inventario;
import com.dondeElPipe.gestorInventario.repository.InventarioRepository;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository repo;

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Crud básico--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    //ver lista completa inventario
    public List<Inventario> listar(){
        return repo.findAll();
    }

    //crear insumo
    
    public Inventario crearInsumo(Inventario insumo){
        if (repo.existsByNombreInsumoIgnoreCase(insumo.getNombreInsumo())) {
            return null; // Duplicado detectado
        }

        Inventario nuevoInsumo = new Inventario();
        // .trim().replaceAll("\\s+", " ") limpia espacios basura intermedios para la visualización
        nuevoInsumo.setNombreInsumo(insumo.getNombreInsumo().trim().replaceAll("\\s+", " "));
        nuevoInsumo.setStockActual(insumo.getStockActual());
        nuevoInsumo.setUnidadMedida(insumo.getUnidadMedida());
        nuevoInsumo.setCategoria(insumo.getCategoria());

        return repo.save(nuevoInsumo);
    }
    
    //delete insumo
    public void eliminarInsumo(Integer id){
        repo.deleteById(id);
    }

    //modificar insumo
    public Inventario actualizarInsumo(Integer id, Inventario insumo){
        insumo.setId(id);
        return repo.save(insumo);
    }

    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+--Funciones especiales--+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--

    //buscar por id
    public Optional<Inventario> buscarId(Integer id){
        return repo.findById(id);
    }

}
