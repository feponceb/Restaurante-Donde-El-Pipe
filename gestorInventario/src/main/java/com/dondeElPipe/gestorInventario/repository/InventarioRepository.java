package com.dondeElPipe.gestorInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorInventario.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    boolean existsByNombreInsumoSinEspacios(@Param("nombre") String nombre);
}
