package com.dondeElPipe.gestorInventario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorInventario.DTO.InventarioDTO;
import com.dondeElPipe.gestorInventario.model.Inventario;
import com.dondeElPipe.gestorInventario.repository.InventarioRepository;

@Service
public class InventarioService {

    @Autowired
    private InventarioRepository repo;

    /**
     * Recibe una lista de ingredientes a descontar (Ej: ["Pan", "Vienesa"])
     */
    public void descontarStock(List<String> ingredientes) {
        for (String nombre : ingredientes) {
            Inventario item = repo.findByNombreIngredienteIgnoreCase(nombre)
                .orElseThrow(() -> new RuntimeException("ERROR Crítico: El ingrediente '" + nombre + "' no existe en bodega."));

            if (item.getStock() < 1) {
                throw new RuntimeException("ERROR de Bodega: Sin stock disponible para el ingrediente: " + nombre);
            }

            item.setStock(item.getStock() - 1);
            repo.save(item);
            System.out.println("LOG INVENTARIO: Se descontó 1 unidad de " + nombre + ". Stock restante: " + item.getStock());
        }
    }

    /**
     * AGREGAR O REABASTECER: Si el ingrediente ya existe por nombre, 
     * suma el nuevo stock al existente en vez de duplicarlo o lanzar error.
     */
    public Inventario agregarOReabastecer(Inventario nuevoItem) {
        Optional<Inventario> itemExistente = repo.findByNombreIngredienteIgnoreCase(nuevoItem.getNombreIngrediente());
        
        if (itemExistente.isPresent()) {
            Inventario itemBd = itemExistente.get();
            // Sumamos el stock entrante al stock actual
            itemBd.setStock(itemBd.getStock() + nuevoItem.getStock());
            return repo.save(itemBd);
        }
        
        // Si no existe, lo crea desde cero
        return repo.save(nuevoItem);
    }

    /**
     * MODIFICAR PRODUCTO: Busca por ID y reemplaza los valores de forma directa.
     */
    public Inventario modificarProducto(Integer id, Inventario itemModificado) {
        Inventario itemBd = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró el producto con ID: " + id));
        
        // Seteamos los nuevos valores pasados por el Body
        itemBd.setNombreIngrediente(itemModificado.getNombreIngrediente());
        itemBd.setStock(itemModificado.getStock());
        
        return repo.save(itemBd);
    }

    /**
     * Obtener todos los elementos actuales de la bodega (Ideal para paneles de administración)
     */
    public List<Inventario> obtenerTodo() {
        return repo.findAll();
    }

}
