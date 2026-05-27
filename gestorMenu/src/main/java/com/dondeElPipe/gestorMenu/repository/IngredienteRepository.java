package com.dondeElPipe.gestorMenu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorMenu.model.Ingrediente;


@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer>{

}
