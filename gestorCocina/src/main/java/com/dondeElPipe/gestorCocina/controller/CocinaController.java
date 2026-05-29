package com.dondeElPipe.gestorCocina.controller;

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

import com.dondeElPipe.gestorCocina.DTO.OrdenCocinaDTO;
import com.dondeElPipe.gestorCocina.model.OrdenCocina;
import com.dondeElPipe.gestorCocina.service.OrdenCocinaService;

@RestController
@RequestMapping("/cocina")
public class CocinaController {

    @Autowired
    private OrdenCocinaService service;

    // ========================================================
    // ENDPOINTS NORMALES (Retornan Entidad Cruda)
    // ========================================================

    // 1. ENDPOINT PUENTE: Recibe la comanda enviada de manera síncrona desde gestorPedidos
    @PostMapping("/recibir-pedido")
    public ResponseEntity<OrdenCocina> recibirPedido(@RequestBody Map<String, Object> pedidoRaw) {
        // Extraemos de forma dinámica el "id" del pedido enviado en el cuerpo JSON
        Integer pedidoId = (Integer) pedidoRaw.get("id");
        if (pedidoId == null) {
            throw new IllegalArgumentException("El cuerpo del pedido no contiene un 'id' válido.");
        }
        OrdenCocina nuevaOrden = service.recibirPedidoEnCocina(pedidoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
    }

    // 2. ACTUALIZAR ESTADO DE LA PREPARACIÓN 
    @PutMapping("/actualizar-estado/{id}")
    public ResponseEntity<OrdenCocina> cambiarEstado(
            @PathVariable Integer id, 
            @RequestBody Map<String, Integer> body) {
            
        Integer estadoId = body.get("estadoId");
        
        if (estadoId == null) {
            throw new IllegalArgumentException("Debe especificar la propiedad 'estadoId' en el cuerpo JSON.");
        }
        
        OrdenCocina actualizada = service.actualizarEstado(id, estadoId);
        return ResponseEntity.ok(actualizada);
    }

    // ========================================================
    // ENDPOINTS DTO (Retornan Salidas Simplificadas)
    // ========================================================

    // 3. MONITOR DE PANTALLA PARA LOS COCINEROS
    @GetMapping("/pantalla-monitor")
    public ResponseEntity<List<OrdenCocinaDTO>> verMonitorCocina() {
        return ResponseEntity.ok(service.listarOrdenesActivas());
    }

}
