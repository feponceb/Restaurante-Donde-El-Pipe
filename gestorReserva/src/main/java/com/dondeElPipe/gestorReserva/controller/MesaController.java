package com.dondeElPipe.gestorReserva.controller;

import java.util.List;
import java.util.Optional;

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

import com.dondeElPipe.gestorReserva.DTO.MesaSImpleDTO;
import com.dondeElPipe.gestorReserva.model.EstadoMesa;
import com.dondeElPipe.gestorReserva.model.Mesa;
import com.dondeElPipe.gestorReserva.service.MesaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reserva/mesas")
public class MesaController {

    @Autowired
    private MesaService service;

    // 1. REGISTRAR NUEVA MESA 
    @PostMapping("/agregar")
    public ResponseEntity<Mesa> crearMesa(@Valid @RequestBody Mesa mesa) {
        Mesa nuevaMesa = service.guardarMesa(mesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMesa);
    }

    // 2. LISTAR TODAS LAS MESAS 
    @GetMapping("/listar")
    public ResponseEntity<List<Mesa>> listar() {
        List<Mesa> mesas = service.listarTodas();
        return ResponseEntity.ok(mesas);
    }

    // 3. BUSCAR MESA POR ID 
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Mesa> buscarPorId(@PathVariable Integer id) {
        Optional<Mesa> mesaOpt = service.buscarPorId(id);
        if (mesaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mesaOpt.get());
    }

    // 4. ACTUALIZAR ESTADO DE LA MESA (Este es el endpoint que llama gestorPedidos síncronamente)
    @PutMapping("/actualizar-estado/{id}")
    public ResponseEntity<String> cambiarEstado(
            @PathVariable Integer id, 
            @RequestBody java.util.Map<String, Integer> body) { // Cambiado a Integer el valor del mapa
            
        // Extraemos el ID numérico del estado enviado en el JSON
        Integer nuevoEstadoId = body.get("nuevoEstado");
        
        if (nuevoEstadoId == null) {
            return ResponseEntity.badRequest().body("Debe especificar la propiedad 'nuevoEstado' con un ID numérico en el cuerpo JSON.");
        }
        
        Mesa mesaActualizada = service.cambiarEstadoMesa(id, nuevoEstadoId);
        
        if (mesaActualizada != null) {
            return ResponseEntity.ok("El estado de la mesa ID " + id + " cambió exitosamente al ID de estado: " + nuevoEstadoId);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se pudo actualizar. Verifique que la mesa ID " + id + " exista y que el ID de estado sea válido.");
        }
    }

    // ========================================================
    // ENDPOINTS DE CONSULTA SIMPLIFICADA (Retornan DTO)
    // ========================================================

    // 3. LISTAR TODAS LAS MESAS SIMPLIFICADAS
    @GetMapping("/dto/listar")
    public ResponseEntity<List<MesaSImpleDTO>> listarDTO() {
        List<MesaSImpleDTO> mesasDto = service.listarTodasDTO();
        return ResponseEntity.ok(mesasDto);
    }

    // 4. BUSCAR DETALLE DE MESA SIMPLIFICADO
    @GetMapping("/dto/buscar/{id}")
    public ResponseEntity<MesaSImpleDTO> buscarPorIdDTO(@PathVariable Integer id) {
        MesaSImpleDTO dto = service.obtenerPorIdDTO(id);
        return ResponseEntity.ok(dto);
    }

}
