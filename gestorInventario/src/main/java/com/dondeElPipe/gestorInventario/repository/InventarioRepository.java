package com.dondeElPipe.gestorInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorInventario.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    boolean existsByNombreInsumoIgnoreCase(String nombre);
}
