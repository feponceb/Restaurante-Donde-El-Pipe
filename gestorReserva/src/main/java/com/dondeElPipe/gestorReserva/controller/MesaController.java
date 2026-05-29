package com.dondeElPipe.gestorReserva.controller;

import java.util.List;
import java.util.Optional;

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

    private final MesaService service;

    // Inyección manual por constructor
    public MesaController(MesaService service) {
        this.service = service;
    }

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
            @RequestBody java.util.Map<String, String> body) {
            
        // Extraemos el valor del JSON enviado en el body
        String estadoString = body.get("nuevoEstado");
        
        if (estadoString == null) {
            return ResponseEntity.badRequest().body("Debe especificar la propiedad 'nuevoEstado' en el cuerpo JSON.");
        }
        
        try {
            // Convertimos el String del JSON al Enum EstadoMesa
            EstadoMesa nuevoEstado = EstadoMesa.valueOf(estadoString);
            
            Mesa mesaActualizada = service.cambiarEstadoMesa(id, nuevoEstado);
            
            if (mesaActualizada != null) {
                return ResponseEntity.ok("El estado de la mesa ID " + id + " cambio a: " + nuevoEstado);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontro la mesa con el ID: " + id);
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("El estado enviado no es valido. Use: Habilitada, Ocupada o Reservada.");
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
