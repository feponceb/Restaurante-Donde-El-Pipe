package com.dondeElPipe.gestorPedidos.DataLoad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorPedidos.model.EstadoPedido;
import com.dondeElPipe.gestorPedidos.model.TipoPedido;
import com.dondeElPipe.gestorPedidos.repository.EstadoPedidoRepository;
import com.dondeElPipe.gestorPedidos.repository.TipoPedidoRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initPedidos(TipoPedidoRepository tipoRepo, EstadoPedidoRepository estadoRepo) {
        return args -> {
            if (tipoRepo.count() == 0 && estadoRepo.count() == 0) {
                // Catálogo de Tipos de Pedido (Numéricos)
                tipoRepo.save(new TipoPedido(1, "LOCAL"));
                tipoRepo.save(new TipoPedido(2, "RETIRO"));
                tipoRepo.save(new TipoPedido(3, "DELIVERY"));

                // Catálogo de Estados de Pedido (Numéricos)
                estadoRepo.save(new EstadoPedido(1, "PENDIENTE"));
                estadoRepo.save(new EstadoPedido(2, "PREPARANDO"));
                estadoRepo.save(new EstadoPedido(3, "ENTREGADO"));
                estadoRepo.save(new EstadoPedido(4, "RECHAZADO"));
            }
        };
    }

}
