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

    // procesar un pago
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@Valid @RequestBody Pago pago) {
        // Ejecuta la lógica de negocio con las llamadas RestTemplate reales
        Pago nuevoPago = service.registrarPagoYNotificar(pago);
        
        if (nuevoPago == null) {
            // Respuesta explícita controlada (PPT Parte 6)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: No se pudo procesar el pago. El pedido número " + pago.getPedidoId() + " no existe en el sistema de Pedidos (Puerto 8083) o la conexión falló.");
        }
    
        // 201 Created para inserciones exitosas
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPago);
    }

    // consultar y verificar el pago de un pedido
    @GetMapping("/verificar-pedido/{pedidoId}")
    public ResponseEntity<?> verificarPagoPedido(@PathVariable Integer pedidoId) {
        // Buscamos a través del service
        Optional<Pago> pagoOpt = service.buscarPagoAprobadoPorPedido(pedidoId);

        if (pagoOpt.isPresent()) {
            return ResponseEntity.ok("EL PEDIDO #" + pedidoId + " SE ENCUENTRA PAGADO.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se registran pagos aprobados para el pedido número: " + pedidoId);
        }
    }

    // Obtener comprobante simplificado del pago en formato DTO
    @GetMapping("/comprobante/{id}")
    public ResponseEntity<?> obtenerComprobante(@PathVariable Integer id) {
        PagoDTO dto = service.obtenerDetallePagoDTO(id);
        
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ningún registro con el ID de pago proporcionado.");
        }
        
        return ResponseEntity.ok(dto);
    }

}
