package com.dondeElPipe.gestorCocina.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dondeElPipe.gestorCocina.DTO.MenuExternoDTO;
import com.dondeElPipe.gestorCocina.DTO.PedidoCompletoDTO;
import com.dondeElPipe.gestorCocina.model.OrdenCocina;
import com.dondeElPipe.gestorCocina.repository.OrdenCocinaRepository;

@Service
public class CocinaService {

    @Autowired
    private OrdenCocinaRepository repo;

    @Autowired
    private RestTemplate restTemplate;

    // Rutas base del ecosistema
    private final String URL_PEDIDOS = "http://localhost:8083/pedidos/buscar/";
    private final String URL_MENU_BUSCAR = "http://localhost:8080/menu/platillos/buscar/";
    private final String URL_INVENTARIO_DESCONTAR = "http://localhost:8081/inventario/descontar";
    
    // Rutas de salida hacia el Gestor de Pedidos Sincronizadas
    private final String URL_PEDIDOS_ENTREGAR = "http://localhost:8083/pedidos/interno/marcar-entregado/";
    
    // CORRECCIÓN: Apuntamos al endpoint estándar de rollback de Pedidos
    private final String URL_PEDIDOS_ABORTAR = "http://localhost:8083/pedidos/rollback/";

    /**
     * Recibe la notificación de pago del gestorPagos.
     */
    public OrdenCocina recibirNuevaOrden(Integer pedidoId) {
        OrdenCocina orden = new OrdenCocina();
        orden.setPedidoId(pedidoId);
        orden.setEstadoPreparation("PENDIENTE");
        orden.setFechaRecepcion(LocalDateTime.now());
        return repo.save(orden);
    }

    /**
     * ENDPOINT 1: Pasa la comanda a "EN_PREPARACION". 
     */
    public OrdenCocina marcarEnPreparacion(Integer id) {
        OrdenCocina orden = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de cocina #" + id + " no encontrada."));

        if ("LISTO".equals(orden.getEstadoPreparation()) || "CANCELADO_SIN_STOCK".equals(orden.getEstadoPreparation())) {
            throw new IllegalStateException("No puedes modificar una orden que ya ha sido cerrada o cancelada.");
        }

        orden.setEstadoPreparation("EN_PREPARACION");
        return repo.save(orden);
    }

    /**
     * ENDPOINT 2: Pasa la comanda a "LISTO".
     * PROTOCOLO DE EMERGENCIA: Si falla el inventario, gatilla el rollback a Pedidos para liberar la mesa.
     */
    public OrdenCocina marcarComoListo(Integer id) {
        OrdenCocina orden = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Orden de cocina #" + id + " no encontrada."));

        if ("LISTO".equals(orden.getEstadoPreparation()) || "CANCELADO_SIN_STOCK".equals(orden.getEstadoPreparation())) {
            return orden;
        }

        Integer pedidoIdOrigen = orden.getPedidoId();

        try {
            // 1. Obtener los platillos asociados desde el MS Pedidos
            PedidoCompletoDTO pedido = restTemplate.getForObject(
                URL_PEDIDOS + pedidoIdOrigen, PedidoCompletoDTO.class);
            
            if (pedido != null && pedido.getPlatillosIds() != null) {
                List<String> todosLosIngredientes = new ArrayList<>();
                
                // 2. Buscar en el MS Menú la lista de ingredientes de cada plato
                for (Integer platoId : pedido.getPlatillosIds()) {
                    MenuExternoDTO menu = restTemplate.getForObject(
                        URL_MENU_BUSCAR + platoId, MenuExternoDTO.class);
                    
                    if (menu != null && menu.getIngredientes() != null) {
                        todosLosIngredientes.addAll(menu.getIngredientes());
                    }
                }
                
                // 3. ENVIAR AL INVENTARIO: Intentar descontar stock
                if (!todosLosIngredientes.isEmpty()) {
                    restTemplate.put(URL_INVENTARIO_DESCONTAR, todosLosIngredientes);
                    System.out.println("LOG: Stock retirado con éxito para la comanda #" + id);
                } else {
                    throw new IllegalStateException("El platillo no tiene ingredientes configurados en el menú.");
                }
            } else {
                throw new IllegalArgumentException("No se encontró información del pedido origen.");
            }
        } catch (Exception e) {
            // =================================================================
            // 🔥 CONTROL DE ERRORES: ACTIVACIÓN DEL PROTOCOLO DE REEMBOLSO 🔥
            // =================================================================
            System.out.println(">>> ALERTA CRÍTICA: Error de stock detectado. Ejecutando Rollback...");
            
            // A) Cancelamos la comanda local en Cocina
            orden.setEstadoPreparation("CANCELADO_SIN_STOCK");
            repo.save(orden);

            // B) Sincronizado: Solicitamos al Gestor de Pedidos que cambie a REEMBOLSADO y libere la mesa
            try {
                String urlEmergencia = URL_PEDIDOS_ABORTAR + pedidoIdOrigen;
                restTemplate.put(urlEmergencia, null);
                System.out.println("LOG EMERGENGIA: Solicitud de anulación enviada a Pedidos exitosamente.");
            } catch (Exception ex) {
                System.out.println(">>> ERROR CRÍTICO EN ROLLBACK: No se pudo conectar con Pedidos: " + ex.getMessage());
            }

            // Relanzamos la excepción original para Postman
            throw new RuntimeException("OPERACIÓN ABORTADA Y PEDIDO REEMBOLSADO: No se retiraron los ingredientes necesarios. Motivo: " + e.getMessage());
        }

        // =================================================================
        // ✅ CAMINO FELIZ: SI EL TRY FUE EXITOSO, SE PROCESA LA ENTREGA
        // =================================================================
        orden.setEstadoPreparation("LISTO");
        OrdenCocina ordenGuardada = repo.save(orden);

        // 4. NOTIFICACIÓN DE ENTREGA EN MESA (Hacia Pedidos -> Cambia mesa a Ocupada)
        try {
            String urlEntrega = URL_PEDIDOS_ENTREGAR + orden.getPedidoId();
            restTemplate.put(urlEntrega, null);
            System.out.println("LOG: Cocina notificó con éxito a Pedidos. Garzón en camino con el plato.");
        } catch (Exception e) {
            System.out.println(">>> ALERTA SISTEMA: Cocina guardó LISTO, pero no logró avisar a Pedidos. Motivo: " + e.getMessage());
        }

        return ordenGuardada;
    }

}
