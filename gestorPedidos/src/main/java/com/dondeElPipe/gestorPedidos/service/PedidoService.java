package com.dondeElPipe.gestorPedidos.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dondeElPipe.gestorPedidos.DTO.DetallePedidoDTO;
import com.dondeElPipe.gestorPedidos.DTO.PedidoSimpleDTO;
import com.dondeElPipe.gestorPedidos.model.DetallePedido;
import com.dondeElPipe.gestorPedidos.model.EstadoPedido;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.model.Plato;
import com.dondeElPipe.gestorPedidos.model.TipoPedido;
import com.dondeElPipe.gestorPedidos.repository.EstadoPedidoRepository;
import com.dondeElPipe.gestorPedidos.repository.PedidoRepository;
import com.dondeElPipe.gestorPedidos.repository.TipoPedidoRepository;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final TipoPedidoRepository tipoRepo;
    private final EstadoPedidoRepository estadoRepo;

    // Inyección manual por constructor de todos los repositorios relacionales
    public PedidoService(PedidoRepository repo, TipoPedidoRepository tipoRepo, EstadoPedidoRepository estadoRepo) {
        this.repo = repo;
        this.tipoRepo = tipoRepo;
        this.estadoRepo = estadoRepo;
    }

    // ========================================================
    // FUNCIONES NORMALES 
    // ========================================================

    // 1. REGISTRAR UN NUEVO PEDIDO
    public Pedido crearPedido(Pedido pedido) {
        
        if (pedido.getTipoPedido() == null || pedido.getTipoPedido().getId() == null) {
            throw new IllegalArgumentException("El tipo de pedido es obligatorio y debe contener un ID válido.");
        }

        // Buscamos el tipo de pedido real en la BD para verificar si existe (1: Local, 2: Delivery, etc.)
        TipoPedido tipoPersistido = tipoRepo.findById(pedido.getTipoPedido().getId())
                .orElseThrow(() -> new IllegalArgumentException("El ID de tipo de pedido especificado no existe en el catálogo."));
        pedido.setTipoPedido(tipoPersistido);

        // REGLA DE NEGOCIO: Si la ID es 1 (LOCAL), exige mesa y se comunica de forma síncrona
        if (tipoPersistido.getId() == 1) {
            if (pedido.getMesaId() == null || pedido.getMesaId() <= 0) {
                throw new IllegalArgumentException("Para pedidos en el LOCAL debe especificar un ID de mesa válido.");
            }
            
            // COMUNICACIÓN SÍNCRONA: Ocupamos la mesa en gestorReserva (Puerto 8084)
            try {
                RestTemplate restTemplate = new RestTemplate();
                String urlReserva = "http://localhost:8084/reserva/mesas/actualizar-estado/" + pedido.getMesaId();
                
                Map<String, String> body = new HashMap<>();
                body.put("nuevoEstado", "Ocupada");
                
                restTemplate.put(urlReserva, body);
            } catch (Exception e) {
                // CORRECCIÓN ESTRICTA: Si el microservicio de reservas está apagado, lanzamos excepción y frenamos el flujo
                throw new IllegalArgumentException("No se pudo registrar el pedido porque el sistema de reservas no está disponible en este momento.");
            }
        } else {
            pedido.setMesaId(null);
        }

        // Forzar estado por defecto al nacer la comanda (ID 1: PENDIENTE en tu catálogo)
        EstadoPedido estadoInicial = estadoRepo.findById(1)
                .orElseThrow(() -> new IllegalStateException("El catálogo de estados (ID 1: Pendiente) no está inicializado en la base de datos."));
        pedido.setEstadoPedido(estadoInicial);
        pedido.setFechaCreacion(LocalDateTime.now());

        // Regla de Negocio: Validar que el pedido no venga vacío
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un platillo en su detalle.");
        }

        RestTemplate restTemplate = new RestTemplate();

        // Bucle tradicional para procesar las líneas del detalle
        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setPedido(pedido); // Amarre de la llave foránea

            // COMUNICACIÓN SÍNCRONA: Buscamos el precio real del plato en gestorMenu (Puerto 8080)
            String urlMenu = "http://localhost:8080/menus/buscar/" + detalle.getPlatoId();
            try {
                Plato plato = restTemplate.getForObject(urlMenu, Plato.class);
                if (plato != null && plato.getPrecio() != null) {
                    detalle.setSubtotal(plato.getPrecio() * detalle.getCantidad());
                }
            } catch (Exception e) {
                detalle.setSubtotal(0.0); // Failsafe si el microservicio de menús está caído
            }
        }

        // Guardamos el pedido en la base de datos local
        Pedido pedidoGuardado = repo.save(pedido);

        // COMUNICACIÓN SÍNCRONA: Enviamos el pedido a gestorCocina (Puerto 8086)
        try {
            String urlCocina = "http://localhost:8086/cocina/recibir-pedido";
            restTemplate.postForObject(urlCocina, pedidoGuardado, Object.class);
            
            // Si la cocina responde exitosamente, avanzamos el estado al ID 2 (PREPARANDO)
            EstadoPedido estadoPreparando = estadoRepo.findById(2).orElse(estadoInicial);
            pedidoGuardado.setEstadoPedido(estadoPreparando);
            pedidoGuardado = repo.save(pedidoGuardado);
        } catch (Exception e) {
            // Failsafe cocina apagada: se queda guardado en estado PENDIENTE de forma segura
        }

        return pedidoGuardado;
    }

    // 2. ACTUALIZAR ESTADO DEL PEDIDO (Relacional)
    public Pedido actualizarEstado(Integer id, EstadoPedido nuevoEstado) {
        Optional<Pedido> pedidoOpt = repo.findById(id);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("No se puede actualizar. No existe el pedido con ID: " + id);
        }

        if (nuevoEstado == null || nuevoEstado.getId() == null) {
            throw new IllegalArgumentException("El objeto de estado y su ID respectivo son obligatorios.");
        }

        // Validamos que la ID del estado realmente exista en el catálogo de la BD
        EstadoPedido estadoPersistido = estadoRepo.findById(nuevoEstado.getId())
                .orElseThrow(() -> new IllegalArgumentException("El ID de estado especificado no existe en la base de datos."));

        Pedido pedido = pedidoOpt.get();
        pedido.setEstadoPedido(estadoPersistido);

        // Si el estado es de cierre (ID 3: ENTREGADO o ID 4: RECHAZADO), liberamos la mesa en gestorReserva
        if ((estadoPersistido.getId() == 3 || estadoPersistido.getId() == 4) && pedido.getMesaId() != null) {
            try {
                RestTemplate restTemplate = new RestTemplate();
                String urlReserva = "http://localhost:8084/reserva/mesas/actualizar-estado/" + pedido.getMesaId();
                        
                Map<String, String> body = new HashMap<>();
                body.put("nuevoEstado", "Habilitada");
                        
                restTemplate.put(urlReserva, body);
            } catch (Exception e) {
                // Failsafe silencioso para asegurar que la actualización de estado local sí se guarde
            }
        }

        return repo.save(pedido);
    }

    // 3. ELIMINAR PEDIDO
    public void eliminarPedido(Integer id) {
        Optional<Pedido> pedidoOpt = repo.findById(id);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("No se puede eliminar. El pedido con ID " + id + " no existe.");
        }
        repo.deleteById(id);
    }

    // ========================================================
    // FUNCIONES DTO 
    // ========================================================

    // 4. BUSCAR UN PEDIDO POR ID (Entrega DTO)
    public PedidoSimpleDTO buscarPorIdDTO(Integer id) {
        Optional<Pedido> pedidoOpt = repo.findById(id);
        if (pedidoOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ningún pedido con el ID: " + id);
        }
        
        Pedido pedido = pedidoOpt.get();
        double total = 0.0;
        for (DetallePedido d : pedido.getDetalles()) {
            total += d.getSubtotal();
        }
        
        return convertirADTO(pedido, total);
    }

    // 5. LISTAR TODOS LOS PEDIDOS (Entrega Lista DTO)
    public List<PedidoSimpleDTO> listarTodosDTO() {
        List<Pedido> pedidos = repo.findAll();
        List<PedidoSimpleDTO> listaDTO = new ArrayList<>();
        
        for (Pedido p : pedidos) {
            double total = 0.0;
            for (DetallePedido d : p.getDetalles()) {
                total += d.getSubtotal();
            }
            listaDTO.add(convertirADTO(p, total));
        }
        return listaDTO;
    }

    // ========================================================
    // MÉTODO AUXILIAR (Extrae los nombres String del catálogo)
    // ========================================================
    private PedidoSimpleDTO convertirADTO(Pedido pedido, Double total) {
        PedidoSimpleDTO dto = new PedidoSimpleDTO();
        dto.setIdPedido(pedido.getId());
        
        // CORRECCIÓN: Extraemos el String usando .getNombre() de las relaciones de BD, no de un Enum .name()
        if (pedido.getTipoPedido() != null) {
            dto.setTipoPedido(pedido.getTipoPedido().getNombre());
        }
        if (pedido.getEstadoPedido() != null) {
            dto.setEstadoPedido(pedido.getEstadoPedido().getNombre());
        }
        
        dto.setUsuarioId(pedido.getUsuarioId());
        dto.setMesaId(pedido.getMesaId());
        dto.setTotalPedido(total);

        List<DetallePedidoDTO> detallesDTO = new ArrayList<>();
        if (pedido.getDetalles() != null) {
            for (DetallePedido d : pedido.getDetalles()) {
                DetallePedidoDTO dDto = new DetallePedidoDTO();
                dDto.setPlatoId(d.getPlatoId());
                dDto.setCantidad(d.getCantidad());
                dDto.setSubtotal(d.getSubtotal());
                detallesDTO.add(dDto);
            }
        }
        dto.setDetalles(detallesDTO);
        return dto;
    }
}
