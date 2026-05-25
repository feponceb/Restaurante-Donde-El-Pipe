package com.dondeElPipe.gestorPedidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorPedidos.model.EstadoPedido;
import com.dondeElPipe.gestorPedidos.model.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer>{

    // Función útil para la cocina: buscar todos los pedidos que estén en un estado específico
    List<Pedido> findByEstado(EstadoPedido estado);
    
    // Función útil para el cajero o administrador: buscar los pedidos asociados a una mesa activa
    List<Pedido> findByMesaIdAndEstado(Integer mesaId, EstadoPedido estado);

}
