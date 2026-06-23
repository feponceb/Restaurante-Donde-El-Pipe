package com.dondeElPipe.gestorPedidos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorPedidos.DTO.PedidoCrearDTO;
import com.dondeElPipe.gestorPedidos.DTO.PedidoRespuestaDTO;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    /**
     * Paso 1: Crear la orden pasándole el DTO limpio.
     */
    @Operation(
        summary = "Iniciar una nueva orden/pedido",
        description = "Recibe los datos básicos del pedido en un DTO e inicializa el flujo del ciclo de vida de la orden"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido iniciado e integrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Error en la solicitud o datos de negocio inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarOrden(@RequestBody PedidoCrearDTO dto) {
        try {
            Pedido pedido = new Pedido();
            pedido.setIdMesa(dto.getIdMesa());
            pedido.setIdGarzon(dto.getIdGarzon());
            pedido.setPlatillosIds(dto.getPlatillosIds());

            PedidoRespuestaDTO respuesta = service.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Paso 2: Endpoint interno que será llamado por el gestorPagos (8085)
     */
    @Operation(
        summary = "Confirmar pago de un pedido (Interno)",
        description = "Endpoint síncrono para que el microservicio de Pagos notifique que el pedido fue liquidado con éxito"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pago procesado y estado del pedido actualizado"),
        @ApiResponse(responseCode = "404", description = "El pedido con el ID especificado no existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/interno/confirmar-pago/{id}")
    public ResponseEntity<?> pagarPedido(@PathVariable Integer id) {
        try {
            Pedido pedidoPagado = service.confirmarPagoYProcesar(id);
            return ResponseEntity.ok(pedidoPagado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Extra: Endpoint de consulta requerido por el gestorPagos y gestorCocina
     */
    @Operation(
        summary = "Buscar pedido por ID",
        description = "Obtiene los detalles operativos de un pedido específico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado con éxito"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPedidoPorId(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Extra: Obtener el historial completo de pedidos.
     */
    @Operation(
        summary = "Listar todos los pedidos",
        description = "Muestra un historial completo de todas las órdenes registradas en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Historial obtenido correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/listar")
    public ResponseEntity<?> listarTodosLosPedidos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    /**
     * Paso 4: Endpoint interno llamado por el gestorCocina (8086) cuando el plato está listo.
     */
    @Operation(
        summary = "Marcar pedido como entregado (Interno)",
        description = "Endpoint síncrono para que Cocina notifique que la orden está lista, disparando la liberación de la mesa"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido marcado como entregado e instrucciones enviadas a Mesas"),
        @ApiResponse(responseCode = "500", description = "Error interno al cambiar estado o al comunicar con el gestor de reservas")
    })
    @PutMapping("/interno/marcar-entregado/{id}")
    public ResponseEntity<?> entregarPedido(@PathVariable Integer id) {
        try {
            Pedido pedidoEntregado = service.marcarComoEntregadoYNotificarMesa(id);
            return ResponseEntity.ok(pedidoEntregado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
