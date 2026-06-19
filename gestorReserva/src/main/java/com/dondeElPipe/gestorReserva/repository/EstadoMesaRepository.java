package com.dondeElPipe.gestorReserva.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorReserva.model.EstadoMesa;

@Repository
public interface EstadoMesaRepository extends JpaRepository<EstadoMesa, Integer>{

}
