package com.dondeElPipe.gestorCocina.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorCocina.model.OrdenCocina;
import com.dondeElPipe.gestorCocina.service.CocinaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/cocina")
public class CocinaController {

    @Autowired
    private CocinaService service;

    /**
     * Recibe la orden enviada por el gestorPagos (8085)
     */
    @Operation(
        summary = "Recibir pedido desde pagos (Interno)",
        description = "Endpoint síncrono diseñado para que el microservicio de Pagos notifique que una orden fue liquidada e inicie su preparación en cocina"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Comanda creada exitosamente en la cola de cocina"),
        @ApiResponse(responseCode = "400", description = "Cuerpo de la solicitud inválido o falta el 'pedidoId'"),
        @ApiResponse(responseCode = "500", description = "Error interno al procesar u obtener datos del pedido")
    })
    @PostMapping("/recibir-pedido")
    public ResponseEntity<?> recibirPedido(@RequestBody Map<String, Object> body) {
        try {
            Integer pedidoId = (Integer) body.get("pedidoId");
            if (pedidoId == null) {
                return ResponseEntity.badRequest().body("Error: El campo 'pedidoId' es obligatorio.");
            }
            
            OrdenCocina nuevaOrden = service.recibirNuevaOrden(pedidoId);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Cambia el estado de una comanda a EN_PREPARACION de forma directa.
     */
    @Operation(
        summary = "Iniciar preparación de comanda",
        description = "Cambia el estado operativo de una comanda en cocina a 'EN_PREPARACION'"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la comanda actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "La comanda no existe o no se encuentra en un estado válido para iniciar"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/comandas/{id}/iniciar")
    public ResponseEntity<?> iniciarPreparacion(@PathVariable Integer id) {
        try {
            OrdenCocina actualizada = service.marcarEnPreparacion(id);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Cambia el estado de una comanda a LISTO y descuenta ingredientes.
     */
    @Operation(
        summary = "Terminar preparación de comanda",
        description = "Finaliza la preparación de la orden, gatilla de forma síncrona el descuento de insumos en el Inventario y notifica al gestor de Pedidos"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Comanda finalizada, stock rebajado y pedido actualizado con éxito"),
        @ApiResponse(responseCode = "409", description = "Conflicto de negocio (Ej: Quiebre de stock en el inventario al intentar descontar)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor o falla de conectividad entre microservicios")
    })
    @PutMapping("/comandas/{id}/terminar")
    public ResponseEntity<?> terminarPreparacion(@PathVariable Integer id) {
        try {
            OrdenCocina actualizada = service.marcarComoListo(id);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}
