package com.dondeElPipe.gestorPedidos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dondeElPipe.gestorPedidos.DTO.MesaReservaDTO;
import com.dondeElPipe.gestorPedidos.DTO.PlatoMenuDTO;
import com.dondeElPipe.gestorPedidos.model.DetallePedido;
import com.dondeElPipe.gestorPedidos.model.EstadoPedido;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.model.TipoPedido;
import com.dondeElPipe.gestorPedidos.repository.PedidoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        
        // 1. VALIDACIÓN DE LA MESA CONTRA GESTOR_RESERVA (Solo si es Local)
        if (pedido.getTipoPedido() == TipoPedido.Local) {
            if (pedido.getMesaId() == null) return null;

            // URL para consultar al microservicio de Reservas
            String urlReserva = "http://localhost:8084/reserva/mesas/buscar/" + pedido.getMesaId();

            try {
                MesaReservaDTO mesaReal = restTemplate.getForObject(urlReserva, MesaReservaDTO.class);

                // Validamos estrictamente si la mesa existe y si está "Habilitada" en el otro sistema
                if (mesaReal == null || !mesaReal.getEstado().equalsIgnoreCase("Habilitada")) {
                    System.out.println("La mesa no está disponible en gestorReserva.");
                    return null; 
                }

                // OPCIONAL: Podríamos hacer un restTemplate.put() aquí para avisarle 
                // al gestorReserva que cambie la mesa a "Ocupada", si tu diseño lo requiere.

            } catch (Exception e) {
                System.out.println("Error al conectar con gestorReserva: " + e.getMessage());
                return null;
            }
        } else {
            pedido.setMesaId(null);
        }

        // 2. VALIDACIÓN DE DETALLES Y PRECIOS CONTRA GESTOR_MENU (Lo que ya tenías impecable)
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) return null;

        double totalGeneral = 0.0;
        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setPedido(pedido);

            String urlMenu = "http://localhost:8080/menu/platillos/buscar/" + detalle.getPlatoId();
            try {
                PlatoMenuDTO platoReal = restTemplate.getForObject(urlMenu, PlatoMenuDTO.class);
                if (platoReal == null || platoReal.getPrecio() == null) return null;

                double subtotalCalculado = platoReal.getPrecio() * detalle.getCantidad();
                detalle.setSubtotal(subtotalCalculado);
                totalGeneral += subtotalCalculado;
            } catch (Exception e) {
                System.out.println("Error con gestorMenu: " + e.getMessage());
                return null;
            }
        }

        pedido.setTotal(totalGeneral);
        pedido.setEstado(EstadoPedido.Pendiente);

        return pedidoRepo.save(pedido);
        }

    public List<Pedido> listarTodos() {
        return pedidoRepo.findAll();
    }

    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepo.findById(id);
    }

}
