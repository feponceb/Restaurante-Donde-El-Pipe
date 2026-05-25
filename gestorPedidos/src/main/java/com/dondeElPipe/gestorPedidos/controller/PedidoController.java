package com.dondeElPipe.gestorPedidos.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorPedidos.DTO.PedidoLegibleDTO;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.service.PedidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    // 1. Endpoint para Listar todos los pedidos
    @GetMapping("/todo")
    public ResponseEntity<List<Pedido>> obtenerTodosLosPedidos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    

    // 2. Endpoint para crear un pedido nuevo (Local, Delivery o Llevar)
    @PostMapping("/nuevo")
    public ResponseEntity<?> crearNuevoPedido(@Valid @RequestBody Pedido pedido) {
        Pedido creado = service.crearPedido(pedido);
        
        // Si el servicio retornó null, significa que falló alguna regla estricta de negocio
        if (creado == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al procesar el pedido. Verifique que: \n"
                        + "1. Si es Local, haya enviado un 'mesaId' existente y HABILITADA.\n"
                        + "2. El pedido no venga sin platos en el detalle.");
        }
        
        // Retornamos un estado 201 Created con el objeto ya calculado por el backend
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable Integer id) {
        Optional<Pedido> pedidoOpt = service.buscarPorId(id);

        if (pedidoOpt.isPresent()) {
            PedidoLegibleDTO legible = service.convertirALegible(pedidoOpt.get());
            return ResponseEntity.ok(legible); // Retorna código 200 con el DTO premium
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El pedido con ID " + id + " no fue encontrado en el sistema."); // Retorna 404
        }
    }

}
