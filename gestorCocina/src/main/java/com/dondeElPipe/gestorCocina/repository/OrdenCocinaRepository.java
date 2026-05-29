package com.dondeElPipe.gestorCocina.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorCocina.model.OrdenCocina;

@Repository
public interface OrdenCocinaRepository extends JpaRepository<OrdenCocina, Integer>{
    List<OrdenCocina> findByEstadoCocinaIn(List<Integer> estadoIds);

}
