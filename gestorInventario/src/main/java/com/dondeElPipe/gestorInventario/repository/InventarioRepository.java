package com.dondeElPipe.gestorInventario.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorInventario.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByNombreIngredienteIgnoreCase(String nombreIngrediente);
}
