package com.dondeElPipe.gestorPedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorPedidos.model.Mesa;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer>{

}
