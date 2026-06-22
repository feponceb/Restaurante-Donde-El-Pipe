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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorCocina.model.OrdenCocina;
import com.dondeElPipe.gestorCocina.service.CocinaService;

@RestController
@RequestMapping("/cocina")
public class CocinaController {

    @Autowired
    private CocinaService service;

    /**
     * Recibe la orden enviada por el gestorPagos (8085)
     * POST: http://localhost:8086/cocina/recibir-pedido
     */
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
     * PUT: http://localhost:8086/cocina/comandas/{id}/iniciar
     */
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
     * PUT: http://localhost:8086/cocina/comandas/{id}/terminar
     */
    @PutMapping("/comandas/{id}/terminar")
    public ResponseEntity<?> terminarPreparacion(@PathVariable Integer id) {
        try {
            OrdenCocina actualizada = service.marcarComoListo(id);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            // Si el inventario falla, aquí devolverá el error controlado
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}
