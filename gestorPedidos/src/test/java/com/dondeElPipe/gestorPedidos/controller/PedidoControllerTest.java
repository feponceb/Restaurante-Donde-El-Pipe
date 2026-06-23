package com.dondeElPipe.gestorPedidos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dondeElPipe.gestorPedidos.DTO.PedidoRespuestaDTO;
import com.dondeElPipe.gestorPedidos.model.Pedido;
import com.dondeElPipe.gestorPedidos.service.PedidoService;

@WebMvcTest(PedidoController.class)
public class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService service;

    @Test
    void iniciarOrden_Exitoso_DeberiaRetornarStatus201() throws Exception {
        // Arrange
        String pedidoJson = """
            {
                "idMesa": 2,
                "idGarzon": 5,
                "platillosIds": [10, 11, 12]
            }
            """;
        PedidoRespuestaDTO respuestaDTO = new PedidoRespuestaDTO();
        when(service.crearPedido(any(Pedido.class))).thenReturn(respuestaDTO);

        // Act & Assert
        mockMvc.perform(post("/pedidos/iniciar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(pedidoJson))
               .andExpect(status().isCreated()); // 201 Created
    }

    @Test
    void iniciarOrden_ConError_DeberiaRetornarStatus400() throws Exception {
        // Arrange
        String pedidoJson = """
            {
                "idMesa": 2,
                "idGarzon": 5,
                "platillosIds": [10]
            }
            """;
        // Simulamos que el service arroja una excepción de negocio (Ej: Mesa ocupada)
        when(service.crearPedido(any(Pedido.class))).thenThrow(new RuntimeException("La mesa no está disponible"));

        // Act & Assert
        mockMvc.perform(post("/pedidos/iniciar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(pedidoJson))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void pagarPedido_Exitoso_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        Pedido pedidoPagado = new Pedido();
        when(service.confirmarPagoYProcesar(id)).thenReturn(pedidoPagado);

        // Act & Assert
        mockMvc.perform(put("/pedidos/interno/confirmar-pago/{id}", id))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void pagarPedido_NoEncontrado_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.confirmarPagoYProcesar(id)).thenThrow(new RuntimeException("El pedido no existe"));

        // Act & Assert
        mockMvc.perform(put("/pedidos/interno/confirmar-pago/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void buscarPedidoPorId_Existente_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        Pedido pedido = new Pedido();
        when(service.buscarPorId(id)).thenReturn(Optional.of(pedido));

        // Act & Assert
        mockMvc.perform(get("/pedidos/buscar/{id}", id))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void buscarPedidoPorId_NoExistente_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/pedidos/buscar/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void listarTodosLosPedidos_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        List<Pedido> historial = List.of(new Pedido());
        when(service.listarTodos()).thenReturn(historial);

        // Act & Assert
        mockMvc.perform(get("/pedidos/listar"))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void entregarPedido_Exitoso_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        Pedido pedidoEntregado = new Pedido();
        when(service.marcarComoEntregadoYNotificarMesa(id)).thenReturn(pedidoEntregado);

        // Act & Assert
        mockMvc.perform(put("/pedidos/interno/marcar-entregado/{id}", id))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void entregarPedido_ErrorInterno_DeberiaRetornarStatus500() throws Exception {
        // Arrange
        Integer id = 1;
        // Simulamos un fallo de comunicación externa con FeignClient hacia el gestor de reservas
        when(service.marcarComoEntregadoYNotificarMesa(id)).thenThrow(new RuntimeException("Error al conectar con el servicio de reservas"));

        // Act & Assert
        mockMvc.perform(put("/pedidos/interno/marcar-entregado/{id}", id))
               .andExpect(status().isInternalServerError()); // 500 Internal Server Error
    }

}
