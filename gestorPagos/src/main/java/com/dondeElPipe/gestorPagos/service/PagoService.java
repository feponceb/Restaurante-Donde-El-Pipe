package com.dondeElPipe.gestorPagos.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dondeElPipe.gestorPagos.DTO.PagoDTO;
import com.dondeElPipe.gestorPagos.model.Pago;
import com.dondeElPipe.gestorPagos.repository.PagoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository repo;

    @Autowired
    private RestTemplate restTemplate; 

    // URLs de comunicación con el ecosistema (Puertos asignados)
    private final String URL_PEDIDOS = "http://localhost:8083/pedidos/buscar/";
    private final String URL_CONFIRMAR_PAGO = "http://localhost:8083/pedidos/interno/confirmar-pago/";
    private final String URL_RESERVAS = "http://localhost:8084/reserva/mesas/actualizar-estado/";
    private final String URL_COCINA = "http://localhost:8086/cocina/recibir-pedido";

    public Pago procesarPagoEstructurado(Pago pagoSolicitud) {
        // 1. Consultar el estado real del pedido al gestorPedidos (8083)
        Map<?, ?> pedidoExterno;
        try {
            pedidoExterno = restTemplate.getForObject(URL_PEDIDOS + pagoSolicitud.getPedidoId(), Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Error: El pedido con ID " + pagoSolicitud.getPedidoId() + " no existe o el microservicio está caído.");
        }

        if (pedidoExterno == null) {
            throw new IllegalArgumentException("Error: No se encontró información para el pedido solicitado.");
        }

        // 2. REGLA DE NEGOCIO 1: Blindaje contra pagos dobles o infinitos
        String estadoActual = (String) pedidoExterno.get("estadoPedido");
        if (estadoActual != null && !estadoActual.equals("ESPERANDO_PAGO")) {
            throw new IllegalStateException("Rechazado: El pedido con ID " + pagoSolicitud.getPedidoId() + 
                    " ya no está disponible para pago. Estado actual: " + estadoActual);
        }

        // 3. REGLA DE NEGOCIO 2: Validación exacta del monto (Ni un peso más, ni un peso menos)
        Double totalRealPedido = ((Number) pedidoExterno.get("totalPagar")).doubleValue();
        if (Math.abs(pagoSolicitud.getMonto() - totalRealPedido) > 0.01) {
            throw new IllegalArgumentException("Rechazado: El monto enviado (" + pagoSolicitud.getMonto() + 
                    ") no coincide exactamente con el total de la comanda (" + totalRealPedido + ").");
        }

        // --- SI PASA TODAS LAS REGLAS, SE INYECTAN LOS DATOS AUTOMÁTICOS ---

        // Usamos tus variables reales: "estado" y "fechaPago" (con la fecha/hora actual del servidor)
        pagoSolicitud.setEstado("APROBADO");
        pagoSolicitud.setFechaPago(LocalDateTime.now());

        // A. Persistencia financiera en tu tabla "pago"
        Pago pagoRegistrado = repo.save(pagoSolicitud);

        // B. Notificar al gestorPedidos (8083) para cerrar el ciclo de cobro (Pasará a "PAGADO")
        restTemplate.put(URL_CONFIRMAR_PAGO + pagoSolicitud.getPedidoId(), null);

        // C. Notificar al gestorReserva (8084) para pasar la mesa a Ocupada (ID Estado: 3)
        try {
            Integer idMesa = (Integer) pedidoExterno.get("idMesa");
            Map<String, Integer> bodyMesa = new HashMap<>();
            bodyMesa.put("nuevoEstado", 3); 
            restTemplate.put(URL_RESERVAS + idMesa, bodyMesa);
        } catch (Exception e) {
            System.out.println(">>> Alerta: No se pudo actualizar el estado de la mesa en Reservas: " + e.getMessage());
        }

        // D. Enviar la comanda limpia al monitor del gestorCocina (8086)
        try {
         Map<String, Object> bodyCocina = new HashMap<>();
         bodyCocina.put("pedidoId", pagoSolicitud.getPedidoId()); // Tu controlador lee exactamente "pedidoId"

            restTemplate.postForObject(URL_COCINA, bodyCocina, Object.class);
        } catch (Exception e) {
            System.out.println(">>> Alerta: No se pudo notificar a la Cocina: " + e.getMessage());
        }

        return pagoRegistrado;
    }

    /**
     * Busca en la base de datos si existe algún pago aprobado ligado a ese ID de pedido.
     */
    public Optional<Pago> buscarPagoAprobadoPorPedido(Integer pedidoId) {
        // Asumiendo que tienes esta consulta derivada o un findByPedidoId en tu repo
        // Si no, puedes usar: return repo.findAll().stream().filter(p -> p.getPedidoId().equals(pedidoId) && "APROBADO".equals(p.getEstado())).findFirst();
        return repo.findAll().stream()
                .filter(p -> p.getPedidoId().equals(pedidoId) && "APROBADO".equals(p.getEstado()))
                .findFirst();
    }

    /**
     * Transforma la entidad de pago en un comprobante DTO limpio.
     */
    public PagoDTO obtenerDetallePagoDTO(Integer id) {
        return repo.findById(id).map(p -> {
            PagoDTO dto = new PagoDTO();
            dto.setId(p.getId());
            dto.setPedidoId(p.getPedidoId());
            dto.setMonto(p.getMonto());
            dto.setMetodoPago(p.getMetodoPago());
            dto.setEstado(p.getEstado());
            dto.setFechaPago(p.getFechaPago());
            return dto;
        }).orElse(null);
    }

}
