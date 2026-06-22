package com.dondeElPipe.gestorPedidos.controller;

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

import com.dondeElPipe.gestorPedidos.DTO.PedidoCrearDTO;
import com.dondeElPipe.gestorPedidos.DTO.PedidoRespuestaDTO;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.service.PedidoService;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService service;

    /**
     * Paso 1: Crear la orden pasándole el DTO limpio desde Postman.
     * POST: http://localhost:8083/pedidos/iniciar
     */
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarOrden(@RequestBody PedidoCrearDTO dto) {
        try {
            Pedido pedido = new Pedido();
            pedido.setIdMesa(dto.getIdMesa());
            pedido.setIdGarzon(dto.getIdGarzon());
            pedido.setPlatillosIds(dto.getPlatillosIds());

            // Invocamos directamente al servicio, que ya nos devuelve el DTO rico en datos
            PedidoRespuestaDTO respuesta = service.crearPedido(pedido);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Paso 2: Endpoint interno que será llamado por el gestorPagos (8085)
     * PUT: http://localhost:8083/pedidos/interno/confirmar-pago/{id}
     */
    @PutMapping("/interno/confirmar-pago/{id}")
    public ResponseEntity<?> pagarPedido(@PathVariable Integer id) {
        try {
            Pedido pedidoPagado = service.confirmarPagoYProcesar(id);
            return ResponseEntity.ok(pedidoPagado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Extra: Endpoint de consulta requerido por el gestorPagos y gestorCocina
     * GET: http://localhost:8083/pedidos/buscar/{id}
     */
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> buscarPedidoPorId(@PathVariable Integer id) {
        // Asumiendo que tienes este método básico de búsqueda en tu repositorio/servicio
        return service.buscarPorId(id) // O repo.findById(id) directamente si lo tienes público
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Extra: Obtener el historial completo de pedidos.
     * GET: http://localhost:8083/pedidos/listar
     */
    @GetMapping("/listar")
    public ResponseEntity<?> listarTodosLosPedidos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    /**
     * Paso 4: Endpoint interno llamado por el gestorCocina (8086) cuando el plato está listo.
     * PUT: http://localhost:8083/pedidos/interno/marcar-entregado/{id}
     */
    @PutMapping("/interno/marcar-entregado/{id}")
    public ResponseEntity<?> entregarPedido(@PathVariable Integer id) {
        try {
            // Aquí llamamos a una función de tu servicio que cambiará el estado del pedido 
            // a "ENTREGADO" y disparará la lógica hacia el microservicio de Reserva Mesas.
            Pedido pedidoEntregado = service.marcarComoEntregadoYNotificarMesa(id);
            return ResponseEntity.ok(pedidoEntregado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
