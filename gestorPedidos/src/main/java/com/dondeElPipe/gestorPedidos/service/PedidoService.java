package com.dondeElPipe.gestorPedidos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dondeElPipe.gestorPedidos.DTO.PlatoMenuDTO;
import com.dondeElPipe.gestorPedidos.model.DetallePedido;
import com.dondeElPipe.gestorPedidos.model.EstadoMesa;
import com.dondeElPipe.gestorPedidos.model.EstadoPedido;
import com.dondeElPipe.gestorPedidos.model.Mesa;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.model.TipoPedido;
import com.dondeElPipe.gestorPedidos.repository.MesaRepository;
import com.dondeElPipe.gestorPedidos.repository.PedidoRepository;

import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepo;

    @Autowired
    private MesaRepository mesaRepo;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    public Pedido crearPedido(Pedido pedido) {
        
        // 1. VALIDACIÓN DE LA MESA (Misma lógica estricta de antes)
        if (pedido.getTipoPedido() == TipoPedido.Local) {
            if (pedido.getMesaId() == null) return null;
            
            Optional<Mesa> mesaOpt = mesaRepo.findById(pedido.getMesaId());
            if (mesaOpt.isEmpty()) return null;

            Mesa mesa = mesaOpt.get();
            if (mesa.getEstado() != EstadoMesa.Habilitada) return null;

            mesa.setEstado(EstadoMesa.Ocupada);
            mesaRepo.save(mesa);
        } else {
            pedido.setMesaId(null);
        }

        // 2. VALIDACIÓN DE DETALLES
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            return null;
        }

        // 3. COMUNICACIÓN REAL ENTRE SERVICIOS Y CÁLCULO DE VALORES
        double totalGeneral = 0.0;

        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setPedido(pedido);

            // URL ejemplo: http://localhost:8080/menu/buscar/1
            String urlMenu = "http://localhost:8080/menu/platillos/buscar/" + detalle.getPlatoId();
            
            try {
                // Hacemos el GET y Spring mapea automáticamente el JSON recibido en nuestro DTO
                PlatoMenuDTO platoReal = restTemplate.getForObject(urlMenu, PlatoMenuDTO.class);
                
                if (platoReal == null || platoReal.getPrecio() == null) {
                    return null; // El plato no existe en el menú o no tiene precio
                }

                // Extraemos el precio real directo desde el DTO del otro microservicio
                double precioReal = platoReal.getPrecio();

                double subtotalCalculado = precioReal * detalle.getCantidad();
                detalle.setSubtotal(subtotalCalculado);
                totalGeneral += subtotalCalculado;

            } catch (Exception e) {
                // Si el gestorMenu está apagado o la URL falla, atrapamos el error para que no se caiga el sistema
                System.out.println("Error de comunicación con gestorMenu: " + e.getMessage());
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
