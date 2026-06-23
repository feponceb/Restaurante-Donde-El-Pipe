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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/reserva/mesas")
public class MesaController {

    @Autowired
    private MesaService service;

    // 1. REGISTRAR NUEVA MESA 
    @Operation(
        summary = "Registrar nueva mesa",
        description = "Crea una nueva mesa física en el sistema de reservas"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Mesa creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/agregar")
    public ResponseEntity<Mesa> crearMesa(@Valid @RequestBody Mesa mesa) {
        Mesa nuevaMesa = service.guardarMesa(mesa);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMesa);
    }

    // 2. LISTAR TODAS LAS MESAS 
    @Operation(
        summary = "Listar todas las mesas",
        description = "Obtiene la lista completa de mesas incluyendo el modelo de datos completo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de mesas obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<Mesa>> listar() {
        List<Mesa> mesas = service.listarTodas();
        return ResponseEntity.ok(mesas);
    }

    // 3. BUSCAR MESA POR ID 
    @Operation(
        summary = "Buscar mesa por ID",
        description = "Obtiene los detalles completos de una mesa específica mediante su identificador único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mesa encontrada correctamente"),
        @ApiResponse(responseCode = "404", description = "La mesa con el ID especificado no existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Mesa> buscarPorId(@PathVariable Integer id) {
        Optional<Mesa> mesaOpt = service.buscarPorId(id);
        if (mesaOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mesaOpt.get());
    }

    // 4. ACTUALIZAR ESTADO DE LA MESA
    @Operation(
        summary = "Actualizar estado de una mesa",
        description = "Cambia el estado operativo de la mesa (Ej: Disponible, Ocupada) usando un mapa de enteros JSON"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado de la mesa actualizado con éxito"),
        @ApiResponse(responseCode = "400", description = "Formato de cuerpo JSON incorrecto"),
        @ApiResponse(responseCode = "404", description = "Mesa o ID de estado no encontrados"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/actualizar-estado/{id}")
    public ResponseEntity<String> cambiarEstado(
            @PathVariable Integer id, 
            @RequestBody java.util.Map<String, Integer> body) {
            
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

    // 5. LISTAR TODAS LAS MESAS SIMPLIFICADAS
    @Operation(
        summary = "Listar todas las mesas (DTO)",
        description = "Obtiene la lista de mesas en formato simplificado para optimizar el rendimiento de la vista"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista DTO obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/dto/listar")
    public ResponseEntity<List<MesaSImpleDTO>> listarDTO() {
        List<MesaSImpleDTO> mesasDto = service.listarTodasDTO();
        return ResponseEntity.ok(mesasDto);
    }

    // 6. BUSCAR DETALLE DE MESA SIMPLIFICADO
    @Operation(
        summary = "Buscar mesa por ID (DTO)",
        description = "Obtiene los detalles simplificados de una mesa específica mediante su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle DTO obtenido correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/dto/buscar/{id}")
    public ResponseEntity<MesaSImpleDTO> buscarPorIdDTO(@PathVariable Integer id) {
        MesaSImpleDTO dto = service.obtenerPorIdDTO(id);
        return ResponseEntity.ok(dto);
    }

}
