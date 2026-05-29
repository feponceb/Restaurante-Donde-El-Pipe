package com.dondeElPipe.gestorPagos.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dondeElPipe.gestorPagos.DTO.PagoDTO;
import com.dondeElPipe.gestorPagos.DTO.PedidoSimpleDTO;
import com.dondeElPipe.gestorPagos.model.Pago;
import com.dondeElPipe.gestorPagos.repository.PagoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository repo;

    public Pago registrarPagoYNotificar(Pago pago) {
        // 1. Instanciamos RestTemplate idéntico a la PPT de tu profesor
        RestTemplate restTemplate = new RestTemplate();

        // ---------------------------------------------------------------------------------
        // COMUNICACIÓN REAL 1: Consultar datos auténticos al MS Pedidos (Puerto 8083)
        // ---------------------------------------------------------------------------------
        String urlPedidos = "http://localhost:8083/pedidos/buscar/" + pago.getPedidoId();
        PedidoSimpleDTO pedidoDto;
        
        try {
            // RestTemplate mapea el JSON automáticamente al objeto. ¡Adiós Warnings de Type Safety!
            pedidoDto = restTemplate.getForObject(urlPedidos, PedidoSimpleDTO.class);
        } catch (Exception e) {
            System.out.println("Error de red o el pedido no existe en el puerto 8083: " + e.getMessage());
            return null; 
        }

        if (pedidoDto == null) {
            return null;
        }

        // Si el microservicio Pedidos confirmó que existe, guardamos el pago localmente en la BD de Pagos
        pago.setEstado("APROBADO");
        pago.setFechaPago(LocalDateTime.now());
        Pago pagoGuardado = repo.save(pago);

        // Extraemos las variables reales usando los métodos GET limpios del objeto DTO
        String tipoPedido = pedidoDto.getTipoPedido(); 
        Integer mesaId = pedidoDto.getMesaId();

        // ---------------------------------------------------------------------------------
        // COMUNICACIÓN REAL 2: Mandar la orden a preparar al MS Cocina (Puerto 8086)
        // ---------------------------------------------------------------------------------
        String urlCocina = "http://localhost:8086/cocina/recibir-pedido";
        Map<String, Object> bodyCocina = new HashMap<>();
        bodyCocina.put("pedidoId", pago.getPedidoId());

        try {
            restTemplate.postForObject(urlCocina, bodyCocina, Map.class);
            System.out.println("Éxito: Pedido enviado al Gestor de Cocina.");
        } catch (Exception e) {
            System.out.println("No se pudo conectar con la Cocina (Puerto 8086): " + e.getMessage());
        }

        // ---------------------------------------------------------------------------------
        // COMUNICACIÓN CONDICIONAL REAL (Rutas lógicas del negocio según el tipo de pedido)
        // ---------------------------------------------------------------------------------
        
        // CASO A: Si es consumo LOCAL, se le debe asignar/cambiar estado a la mesa obligatoriamente
        if ("LOCAL".equals(tipoPedido)) {
            if (mesaId != null) {
                String urlReserva = "http://localhost:8084/reserva/mesas/cambiar-estado/" + mesaId;
                try {
                    restTemplate.put(urlReserva, null); 
                    System.out.println("Éxito: Mesa " + mesaId + " cambiada a estado OCUPADA en reservas.");
                } catch (Exception e) {
                    System.out.println("No se pudo actualizar la mesa en reservas (Puerto 8084): " + e.getMessage());
                }
            } else {
                System.out.println("Advertencia: El pedido es LOCAL pero no venía con ninguna mesaId asignada.");
            }

        // CASO B: Si es para DELIVERY, se genera automáticamente la orden de despacho
        } else if ("DELIVERY".equals(tipoPedido)) {
            String urlDelivery = "http://localhost:8087/delivery/crear";
            Map<String, Object> bodyDelivery = new HashMap<>();
            bodyDelivery.put("pedidoId", pago.getPedidoId());
            bodyDelivery.put("direccion", "Dirección registrada por el cliente"); 

            try {
                restTemplate.postForObject(urlDelivery, bodyDelivery, Map.class);
                System.out.println("Éxito: Orden de despacho generada en Gestor de Delivery.");
            } catch (Exception e) {
                System.out.println("No se pudo conectar con el Delivery (Puerto 8087): " + e.getMessage());
            }
        }
        // CASO C: Si es RETIRO, simplemente no entra a ningún IF (no requiere mesa ni delivery) 
        // pero ya se envió a cocina en el paso anterior.

        return pagoGuardado;
    }

    // ========================================================
    // LOGICA DE LECTURA (Manejo de Optionals y DTOs para consultas)
    // ========================================================
    
    // Retorna Optional directo para la verificación condicional del controlador
    public Optional<Pago> buscarPagoAprobadoPorPedido(Integer pedidoId) {
        return repo.findByPedidoIdAndEstado(pedidoId, "APROBADO");
    }

    // Consulta limpia estructurada en DTO para reportes o auditorías de caja
    public PagoDTO obtenerDetallePagoDTO(Integer id) {
        Pago pago = repo.findById(id).orElse(null);
        if (pago == null) return null;

        return new PagoDTO(
            pago.getId(),
            pago.getPedidoId(),
            pago.getMonto(),
            pago.getMetodoPago(),
            pago.getEstado(),
            pago.getFechaPago()
        );
    }

}
