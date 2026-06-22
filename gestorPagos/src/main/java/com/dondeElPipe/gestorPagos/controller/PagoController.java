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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService service;

    /**
     * Endpoint para procesar un nuevo pago.
     * Este método inicia la cadena de comunicación con Pedidos, Reservas y Cocina.
     * POST: http://localhost:8085/pagos/procesar
     */
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@Valid @RequestBody Pago pago) {
        try {
            // Sincronizado con el nombre real del método en tu PagoService estructurado
            Pago nuevoPago = service.procesarPagoEstructurado(pago);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Captura los candados de seguridad (Monto incorrecto o ya pagado) y responde un 400 limpio
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Captura errores de caídas de microservicios o fallos de base de datos
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Endpoint para verificar si un pedido ya tiene un pago aprobado.
     * GET: http://localhost:8085/pagos/verificar-pedido/{pedidoId}
     */
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
     * GET: http://localhost:8085/pagos/comprobante/{id}
     */
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
