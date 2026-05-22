package com.dondeElPipe.gestorInventario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorInventario.model.CategoriaInsumo;

@Repository
public interface CategoriaInsumoRepository extends JpaRepository<CategoriaInsumo, Integer>{


}
