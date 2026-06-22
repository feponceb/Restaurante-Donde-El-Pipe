package com.dondeElPipe.gestorPedidos.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dondeElPipe.gestorPedidos.DTO.PedidoRespuestaDTO;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repo;

    @Autowired
    private RestTemplate restTemplate;

    // Endpoints de comunicación interna
    private final String URL_MENU = "http://localhost:8080/menu/platillos/buscar/";
    private final String URL_USUARIOS = "http://localhost:8082/usuarios/buscar/";
    
    // URL base corregida apuntando al controlador del microservicio de Reserva Mesas
    private final String URL_RESERVA_MESAS = "http://localhost:8082/reserva/mesas/actualizar-estado/";

    /**
     * Registra un nuevo pedido, calcula dinámicamente su valor total
     * y genera una respuesta enriquecida con los nombres del menú de forma segura.
     */
    public PedidoRespuestaDTO crearPedido(Pedido pedido) {
        // 1. Verificación obligatoria de personal activo (Previene el /buscar/null)
        if (pedido.getIdGarzon() == null || pedido.getIdGarzon() == 0) {
            throw new IllegalArgumentException("Error: El campo 'idGarzon' es mandatorio.");
        }
        try {
            restTemplate.getForObject(URL_USUARIOS + pedido.getIdGarzon(), Object.class);
        } catch (Exception e) {
            throw new RuntimeException("Error: El garzón con ID " + pedido.getIdGarzon() + " no existe en el sistema.");
        }

        // 2. Estado inicial de ciclo de vida
        pedido.setEstadoPedido("ESPERANDO_PAGO");

        // 3. Consulta al catálogo de menús (Precios y Nombres dinámicos)
        double sumaTotal = 0.0;
        List<String> nombresPlatillos = new ArrayList<>();

        if (pedido.getPlatillosIds() != null && !pedido.getPlatillosIds().isEmpty()) {
            for (Integer platoId : pedido.getPlatillosIds()) {
                try {
                    String urlMenuPlato = URL_MENU + platoId;
                    
                    // Recibimos de forma genérica como Mapa para inspeccionar las claves reales del JSON
                    Map<?, ?> mapaPlato = restTemplate.getForObject(urlMenuPlato, Map.class);
                    
                    if (mapaPlato != null) {
                        // 3.1 Extraer el precio con seguridad
                        Object precioObj = mapaPlato.get("precio");
                        if (precioObj != null) {
                            sumaTotal += ((Number) precioObj).doubleValue();
                        }
                        
                        // 3.2 Inspección elástica del nombre (Caza variantes comunes)
                        String nombreDetectado = null;
                        if (mapaPlato.containsKey("nombre") && mapaPlato.get("nombre") != null) {
                            nombreDetectado = (String) mapaPlato.get("nombre");
                        } else if (mapaPlato.containsKey("nombrePlatillo") && mapaPlato.get("nombrePlatillo") != null) {
                            nombreDetectado = (String) mapaPlato.get("nombrePlatillo");
                        } else if (mapaPlato.containsKey("nombrePlato") && mapaPlato.get("nombrePlato") != null) {
                            nombreDetectado = (String) mapaPlato.get("nombrePlato");
                        }
                        
                        // Inyección del resultado en la lista del DTO
                        if (nombreDetectado != null && !nombreDetectado.trim().isEmpty()) {
                            nombresPlatillos.add(nombreDetectado);
                        } else {
                            nombresPlatillos.add("Platillo Existente (ID: " + platoId + " - Campo nombre nulo)");
                        }
                    }
                } catch (Exception e) {
                    // Fallback controlado si un plato pierde conexión o es removido
                    nombresPlatillos.add("Platillo Desconocido (ID: " + platoId + ")");
                }
            }
        }
        
        pedido.setTotalPagar(sumaTotal);

        // 4. Persistencia en base de datos local
        Pedido pedidoGuardado = repo.save(pedido);

        // 5. Ensamblaje del DTO enriquecido de cara al cliente/Postman
        PedidoRespuestaDTO respuesta = new PedidoRespuestaDTO();
        respuesta.setId(pedidoGuardado.getId());
        respuesta.setIdMesa(pedidoGuardado.getIdMesa());
        respuesta.setIdGarzon(pedidoGuardado.getIdGarzon());
        respuesta.setEstadoPedido(pedidoGuardado.getEstadoPedido());
        respuesta.setTotalPagar(pedidoGuardado.getTotalPagar());
        respuesta.setNombresPlatillos(nombresPlatillos);

        return respuesta;
    }

    /**
     * Orquestación de estados internos gatillada por la pasarela de pagos.
     */
    public Pedido confirmarPagoYProcesar(Integer pedidoId) {
        Pedido pedido = repo.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + pedidoId));
        
        pedido.setEstadoPedido("PAGADO");
        return repo.save(pedido);
    }

    /**
     * PASO 4 (Camino Feliz): Cambia el estado del pedido a ENTREGADO cuando la cocina termina.
     * Envía un JSON con 'nuevoEstado: 2' (Ocupada) al MS Reserva Mesas mediante RestTemplate.
     */
    public Pedido marcarComoEntregadoYNotificarMesa(Integer pedidoId) {
        Pedido pedido = repo.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido #" + pedidoId + " no encontrado en el sistema."));

        pedido.setEstadoPedido("ENTREGADO");
        Pedido pedidoActualizado = repo.save(pedido);
        System.out.println("LOG PEDIDOS: El pedido #" + pedidoId + " ha sido guardado como ENTREGADO.");

        try {
            String urlMesasCompleta = URL_RESERVA_MESAS + pedido.getIdMesa();
            
            // Construimos el payload esperado por el @RequestBody del MesaController: {"nuevoEstado": 2}
            Map<String, Integer> bodyMap = new HashMap<>();
            bodyMap.put("nuevoEstado", 2);
            
            // Enviamos el mapa en el cuerpo de la solicitud PUT
            restTemplate.put(urlMesasCompleta, bodyMap);
            System.out.println("LOG PEDIDOS: Mesa #" + pedido.getIdMesa() + " actualizada a OCUPADA (estado=2).");
        } catch (Exception e) {
            System.out.println(">>> AVISO: El pedido cambió a ENTREGADO, pero falló el aviso a Reserva Mesas: " + e.getMessage());
        }

        return pedidoActualizado;
    }

    /**
     * PROTOCOLO DE EMERGENCIA: Cancela el pedido por falta de insumos, cambia el estado a REEMBOLSADO
     * y envía un JSON con 'nuevoEstado: 1' para liberar y habilitar la mesa de forma síncrona.
     */
    public void procesarReembolsoYCancelarMesa(Integer pedidoId) {
        Pedido pedido = repo.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido #" + pedidoId + " no hallado en Rollback."));
        
        pedido.setEstadoPedido("REEMBOLSADO_SIN_STOCK");
        repo.save(pedido);
        System.out.println("LOG EMERGENGIA: Pedido #" + pedidoId + " actualizado a REEMBOLSADO.");

        try {
            String urlLiberarMesa = URL_RESERVA_MESAS + pedido.getIdMesa();
            
            // Construimos el payload esperado por el @RequestBody del MesaController: {"nuevoEstado": 1}
            Map<String, Integer> bodyMap = new HashMap<>();
            bodyMap.put("nuevoEstado", 1);
            
            // Despachamos el mapa en el cuerpo del PUT
            restTemplate.put(urlLiberarMesa, bodyMap);
            System.out.println("LOG EMERGENGIA: Mesa #" + pedido.getIdMesa() + " liberada con éxito a HABILITADA (estado=1).");
        } catch (Exception e) {
            System.out.println(">>> ALERTA ROLLBACK: El pedido se reembolsó, pero la mesa no se pudo liberar: " + e.getMessage());
        }
    }

    /**
     * Recuperación de entidad por identificador único.
     */
    public Optional<Pedido> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    /**
     * Consulta masiva del historial de comandas.
     */
    public List<Pedido> listarTodos() {
        return repo.findAll();
    }
}
