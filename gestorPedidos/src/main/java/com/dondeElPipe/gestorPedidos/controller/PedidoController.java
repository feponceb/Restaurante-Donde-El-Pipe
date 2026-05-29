package com.dondeElPipe.gestorPedidos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorPedidos.DTO.PedidoSimpleDTO;
import com.dondeElPipe.gestorPedidos.model.EstadoPedido;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    // 1. CREAR NUEVO PEDIDO (Retorna Entidad Cruda)
    @PostMapping("/nuevo")
    public ResponseEntity<Pedido> crearNuevoPedido(@Valid @RequestBody Pedido pedido) {
        Pedido nuevo = service.crearPedido(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    // 2. ACTUALIZAR ESTADO (Retorna Entidad Cruda)
    @PutMapping("/actualizar-estado/{id}")
    public ResponseEntity<Pedido> actualizarEstadoPedido(@PathVariable Integer id, @RequestBody EstadoPedido nuevoEstado) {
        Pedido actualizado = service.actualizarEstado(id, nuevoEstado);
        return ResponseEntity.ok(actualizado);
    }

    // 3. BUSCAR PEDIDO POR ID (Retorna DTO)
    @GetMapping("/buscar/{id}")
    public ResponseEntity<PedidoSimpleDTO> obtenerPorId(@PathVariable Integer id) {
        PedidoSimpleDTO dto = service.buscarPorIdDTO(id);
        return ResponseEntity.ok(dto);
    }

    // 4. LISTAR TODOS LOS PEDIDOS (Retorna Lista DTO)
    @GetMapping("/todo")
    public ResponseEntity<List<PedidoSimpleDTO>> obtenerTodos() {
        List<PedidoSimpleDTO> lista = service.listarTodosDTO();
        return ResponseEntity.ok(lista);
    }

    // 5. ELIMINAR PEDIDO
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> borrarPedido(@PathVariable Integer id) {
        service.eliminarPedido(id);
        return ResponseEntity.ok("El pedido con ID " + id + " fue eliminado exitosamente.");
    }

}
