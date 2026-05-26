package com.dondeElPipe.gestorPedidos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dondeElPipe.gestorPedidos.DTO.DetallePedidoDTO;
import com.dondeElPipe.gestorPedidos.DTO.MesaReservaDTO;
import com.dondeElPipe.gestorPedidos.DTO.PedidoLegibleDTO;
import com.dondeElPipe.gestorPedidos.DTO.PlatoMenuDTO;
import com.dondeElPipe.gestorPedidos.DTO.UsuarioDTO;
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

    public PedidoLegibleDTO convertirALegible(Pedido pedido) {
        PedidoLegibleDTO dto = new PedidoLegibleDTO();
        dto.setIdPedido(pedido.getId());
        dto.setNumeroMesa(pedido.getMesaId());
        dto.setTipoPedido(pedido.getTipoPedido());
        dto.setTotalPagar(pedido.getTotal());
        dto.setEstado(pedido.getEstado());
        dto.setFechaCreacion(pedido.getFechaCreacion());

        // Supongamos que el microservicio de usuarios corre en el puerto 8081
        String urlUsuario = "http://localhost:8082/usuarios/buscar/" + pedido.getUsuarioId();
        try {
            UsuarioDTO usuarioReal = restTemplate.getForObject(urlUsuario, UsuarioDTO.class);
            if (usuarioReal != null) {
                // Unimos nombre y apellido en un solo String para el DTO final
                dto.setNombreGarzon(usuarioReal.getNombre() + " " + usuarioReal.getApellido());
            } else {
                dto.setNombreGarzon("Garzón Desconocido (ID: " + pedido.getUsuarioId() + ")");
            }
        } catch (Exception e) {
            // Por ahora, mientras no tengas el MS Usuarios corriendo, entrará siempre aquí
            // Esto evita que tu código falle o se caiga en el testeo, mostrando un texto limpio.
            dto.setNombreGarzon("Garzón ID: " + pedido.getUsuarioId() + " (MS Usuarios Offline)");
        }

        // El mapeo de los platos queda exactamente igual como ya lo tenías impecable:
        List<DetallePedidoDTO> detallesLegibles = pedido.getDetalles().stream().map(detalle -> {
            DetallePedidoDTO detalleDto = new DetallePedidoDTO();
            detalleDto.setCantidad(detalle.getCantidad());
            detalleDto.setSubtotal(detalle.getSubtotal());

            String urlMenu = "http://localhost:8080/menu/platillos/buscar/" + detalle.getPlatoId();
            try {
                PlatoMenuDTO plato = restTemplate.getForObject(urlMenu, PlatoMenuDTO.class);
                if (plato != null) {
                    detalleDto.setNombrePlato(plato.getNombrePlato());
                } else {
                    detalleDto.setNombrePlato("Plato Desconocido (ID: " + detalle.getPlatoId() + ")");
                }
            } catch (Exception e) {
                detalleDto.setNombrePlato("Error al cargar nombre (ID: " + detalle.getPlatoId() + ")");
            }
            return detalleDto;
        }).toList();

        dto.setPlatosPedidos(detallesLegibles);
        return dto;
    }

}
