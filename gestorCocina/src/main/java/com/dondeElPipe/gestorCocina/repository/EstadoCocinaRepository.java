package com.dondeElPipe.gestorCocina.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorCocina.model.EstadoCocina;

@Repository
public interface EstadoCocinaRepository extends JpaRepository<EstadoCocina, Integer>{
    Optional<EstadoCocina> findByNombreIgnoreCase(String nombre);

}
