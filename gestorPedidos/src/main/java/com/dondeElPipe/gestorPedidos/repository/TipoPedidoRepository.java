package com.dondeElPipe.gestorPedidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorPedidos.model.TipoPedido;

@Repository
public interface TipoPedidoRepository extends JpaRepository<TipoPedido, Integer>{

}
