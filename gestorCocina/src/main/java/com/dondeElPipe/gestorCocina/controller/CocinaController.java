package com.dondeElPipe.gestorCocina.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorCocina.DTO.OrdenCocinaDTO;
import com.dondeElPipe.gestorCocina.service.OrdenCocinaService;

@RestController
@RequestMapping("/cocina")
public class CocinaController {

    @Autowired
    private OrdenCocinaService service;

    // Enviar una nueva orden a la cola de la cocina
    @PostMapping("/nueva-orden")
    public ResponseEntity<?> simularIngresoCocina(@RequestParam Integer pedidoId) {
        OrdenCocinaDTO nuevaOrden = service.recibirPedidoEnCocina(pedidoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaOrden);
    }

    // Ver la pantalla del chef (órdenes que se deben preparar)
    @GetMapping("/pantalla-monitor")
    public ResponseEntity<List<OrdenCocinaDTO>> verMonitorCocina() {
        return ResponseEntity.ok(service.listarOrdenesActivas());
    }

    // Cambiar estado a PREPARANDO o LISTO
    @PutMapping("/actualizar-estado/{id}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @RequestParam String estado) {
        OrdenCocinaDTO actualizada = service.actualizarEstado(id, estado);

        if (actualizada != null) {
            return ResponseEntity.ok(actualizada);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("La orden de cocina con ID " + id + " no existe.");
        }
    }

}
