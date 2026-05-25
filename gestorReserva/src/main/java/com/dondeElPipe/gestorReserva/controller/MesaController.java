package com.dondeElPipe.gestorReserva.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorReserva.model.Mesa;
import com.dondeElPipe.gestorReserva.service.MesaService;

@RestController
@RequestMapping("/reserva/mesas")
public class MesaController {

    @Autowired
    private MesaService mesaService; // Inyectamos el Service recién creado

    // 1. Ver todas las mesas (para testear que cargó la precarga)
    @GetMapping("/todo")
    public ResponseEntity<List<Mesa>> obtenerTodas() {
        return ResponseEntity.ok(mesaService.listarTodas());
    }

    // 2. EL ENDPOINT CLAVE: El que consume gestorPedidos
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> obtenerMesaPorId(@PathVariable Integer id) {
        Optional<Mesa> mesa = mesaService.buscarPorId(id);
        
        if (mesa.isPresent()) {
            return ResponseEntity.ok(mesa.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mesa no encontrada");
        }
    }

}
