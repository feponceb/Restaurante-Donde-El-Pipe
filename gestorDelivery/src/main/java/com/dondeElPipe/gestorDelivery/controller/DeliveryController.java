package com.dondeElPipe.gestorDelivery.controller;

import java.util.List;
import java.util.Map;

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

import com.dondeElPipe.gestorDelivery.DTO.DespachoDTO;
import com.dondeElPipe.gestorDelivery.model.Despacho;
import com.dondeElPipe.gestorDelivery.service.DespachoService;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DespachoService service;

    // ========================================================
    // ENDPOINTS NORMALES
    // ========================================================

    // 1. Crear orden de despacho inicial
    @PostMapping("/crear")
    public ResponseEntity<Despacho> crearDespacho(@RequestBody Map<String, Object> body) {
        Integer pedidoId = (Integer) body.get("pedidoId");
        String direccion = (String) body.get("direccion");
        
        Despacho nuevoDespacho = service.registrarDespachoBase(pedidoId, direccion);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoDespacho);
    }

    // 2. Repartidor inicia el viaje asignándose a la ruta
    @PutMapping("/iniciar-ruta/{id}")
    public ResponseEntity<Despacho> iniciarViaje(
            @PathVariable Integer id, 
            @RequestBody Map<String, Integer> body) {
            
        Integer repartidorId = body.get("repartidorId");
        Despacho actualizado = service.iniciarRuta(id, repartidorId);
        return ResponseEntity.ok(actualizado);
    }

    // 3. Confirmar entrega en domicilio del cliente
    @PutMapping("/entregar/{id}")
    public ResponseEntity<Despacho> confirmarEntrega(@PathVariable Integer id) {
        Despacho entregado = service.marcarComoEntregado(id);
        return ResponseEntity.ok(entregado);
    }

    // ========================================================
    // ENDPOINTS DTO
    // ========================================================

    // 4. Rastreo de despacho por el ID del Pedido original
    @GetMapping("/buscar-por-pedido/{pedidoId}")
    public ResponseEntity<DespachoDTO> rastrearPorPedido(@PathVariable Integer pedidoId) {
        return ResponseEntity.ok(service.obtenerPorPedidoIdDTO(pedidoId));
    }

    // 5. Historial general de despachos para el panel de administración
    @GetMapping("/historial")
    public ResponseEntity<List<DespachoDTO>> verHistorial() {
        return ResponseEntity.ok(service.listarTodosDTO());
    }

}
