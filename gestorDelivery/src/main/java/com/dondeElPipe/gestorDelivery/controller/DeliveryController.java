package com.dondeElPipe.gestorDelivery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorDelivery.DTO.DespachoDTO;
import com.dondeElPipe.gestorDelivery.service.DespachoService;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DespachoService service;

    // Crear orden de despacho inicial
    @PostMapping("/crear")
    public ResponseEntity<?> crearDespacho(@RequestParam Integer pedidoId, @RequestParam String direccion) {
        DespachoDTO nuevoDespacho = service.registrarDespachoBase(pedidoId, direccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDespacho);
    }

    // Repartidor inicia el viaje
    @PutMapping("/iniciar-ruta/{id}")
    public ResponseEntity<?> iniciarViaje(@PathVariable Integer id, @RequestParam Integer repartidorId) {
        DespachoDTO actualizado = service.iniciarRuta(id, repartidorId);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El registro de despacho no existe.");
    }

    // Confirmar entrega en domicilio
    @PutMapping("/entregar/{id}")
    public ResponseEntity<?> confirmarEntrega(@PathVariable Integer id) {
        DespachoDTO entregado = service.marcarComoEntregado(id);
        if (entregado != null) {
            return ResponseEntity.ok(entregado);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("El registro de despacho no existe.");
    }

}
