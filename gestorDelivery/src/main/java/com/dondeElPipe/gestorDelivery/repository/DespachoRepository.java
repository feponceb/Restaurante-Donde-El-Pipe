package com.dondeElPipe.gestorDelivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorDelivery.model.Despacho;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Integer>{
    Optional<Despacho> findByPedidoId(Integer pedidoId);

}
