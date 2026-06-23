package com.dondeElPipe.gestorPagos.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorPagos.DTO.PagoDTO;
import com.dondeElPipe.gestorPagos.model.Pago;
import com.dondeElPipe.gestorPagos.service.PagoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService service;

    /**
     * Endpoint para procesar un nuevo pago.
     */
    @Operation(
        summary = "Procesar un nuevo pago",
        description = "Registra un pago, valida el monto de la orden e inicia la cadena de comunicación síncrona con Pedidos, Reservas y Cocina"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pago procesado y aprobado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o inconsistencia en las reglas de negocio (monto incorrecto o pedido ya liquidado)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor o falla en la comunicación con el microservicio de Pedidos")
    })
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@Valid @RequestBody Pago pago) {
        try {
            Pago nuevoPago = service.procesarPagoEstructurado(pago);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Endpoint para verificar si un pedido ya tiene un pago aprobado.
     */
    @Operation(
        summary = "Verificar estado de pago por ID de Pedido",
        description = "Consulta de forma directa si un ID de pedido específico cuenta con una transacción aprobada y guardada en el sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "El pedido consultado ya se encuentra pagado y aprobado"),
        @ApiResponse(responseCode = "404", description = "No se registran transacciones aprobadas para el ID de pedido provisto"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/verificar-pedido/{pedidoId}")
    public ResponseEntity<?> verificarPagoPedido(@PathVariable Integer pedidoId) {
        Optional<Pago> pagoOpt = service.buscarPagoAprobadoPorPedido(pedidoId);

        if (pagoOpt.isPresent()) {
            return ResponseEntity.ok("EL PEDIDO #" + pedidoId + " SE ENCUENTRA PAGADO Y APROBADO.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se registran pagos aprobados para el pedido número: " + pedidoId);
        }
    }

    /**
     * Endpoint para obtener un comprobante simplificado en formato DTO.
     */
    @Operation(
        summary = "Obtener comprobante de pago (DTO)",
        description = "Recupera los detalles mínimos y esenciales de una transacción en formato simplificado (DTO) para la emisión del ticket o boleta"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalles del comprobante DTO obtenidos correctamente"),
        @ApiResponse(responseCode = "404", description = "No se encontró ningún registro de pago asociado al ID enviado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/comprobante/{id}")
    public ResponseEntity<?> obtenerComprobante(@PathVariable Integer id) {
        PagoDTO dto = service.obtenerDetallePagoDTO(id);
        
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ningún registro de pago con el ID: " + id);
        }
        
        return ResponseEntity.ok(dto);
    }

}
