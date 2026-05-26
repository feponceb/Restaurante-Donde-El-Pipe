package com.dondeElPipe.gestorPagos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorPagos.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer>{
    Optional<Pago> findByPedidoIdAndEstado(Integer pedidoId, String estado);

}
