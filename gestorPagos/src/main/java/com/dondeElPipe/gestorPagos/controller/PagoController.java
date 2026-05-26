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
        // Delegamos todo el flujo al service
        PagoDTO nuevoPagoDTO = service.registrarPago(pago);
        
        // Retornamos la respuesta correcta estructurada con un 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPagoDTO);
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

}
