package com.dondeElPipe.gestorPagos.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dondeElPipe.gestorPagos.DTO.PagoDTO;
import com.dondeElPipe.gestorPagos.model.Pago;
import com.dondeElPipe.gestorPagos.service.PagoService;

@WebMvcTest(PagoController.class)
public class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoService service;

    @Test
    void procesarPago_Exitoso_DeberiaRetornarStatus201() throws Exception {
        // Arrange - JSON estructurado con las validaciones de tu entidad Pago
        String pagoJson = """
            {
                "pedidoId": 10,
                "monto": 25000.0,
                "metodoPago": "TARJETA"
            }
            """;
        Pago pagoCreado = new Pago(1, 10, 25000.0, "TARJETA", "APROBADO", LocalDateTime.now());
        when(service.procesarPagoEstructurado(any(Pago.class))).thenReturn(pagoCreado);

        // Act & Assert
        mockMvc.perform(post("/pagos/procesar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(pagoJson))
               .andExpect(status().isCreated()); // 201 Created
    }

    @Test
    void procesarPago_ReglaNegocioInvalida_DeberiaRetornarStatus400() throws Exception {
        // Arrange - JSON estructuralmente válido, pero que rompe reglas de negocio en el Service
        String pagoJson = """
            {
                "pedidoId": 10,
                "monto": 500.0,
                "metodoPago": "EFECTIVO"
            }
            """;
        // Simulamos la excepción capturada explícitamente en tu controlador (IllegalArgumentException)
        when(service.procesarPagoEstructurado(any(Pago.class)))
               .thenThrow(new IllegalArgumentException("El monto no coincide con el total del pedido"));

        // Act & Assert
        mockMvc.perform(post("/pagos/procesar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(pagoJson))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void procesarPago_FallaServicio_DeberiaRetornarStatus500() throws Exception {
        // Arrange
        String pagoJson = """
            {
                "pedidoId": 10,
                "monto": 15000.0,
                "metodoPago": "TRANSFERENCIA"
            }
            """;
        // Simulamos una excepción genérica (Ej: Caída de comunicación síncrona con el Gestor de Pedidos)
        when(service.procesarPagoEstructurado(any(Pago.class)))
               .thenThrow(new RuntimeException("Error de comunicación de red"));

        // Act & Assert
        mockMvc.perform(post("/pagos/procesar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(pagoJson))
               .andExpect(status().isInternalServerError()); // 500 Internal Server Error
    }

    @Test
    void verificarPagoPedido_Aprobado_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer pedidoId = 10;
        Pago pagoExistente = new Pago();
        when(service.buscarPagoAprobadoPorPedido(pedidoId)).thenReturn(Optional.of(pagoExistente));

        // Act & Assert
        mockMvc.perform(get("/pagos/verificar-pedido/{pedidoId}", pedidoId))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void verificarPagoPedido_NoRegistrado_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer pedidoId = 99;
        when(service.buscarPagoAprobadoPorPedido(pedidoId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/pagos/verificar-pedido/{pedidoId}", pedidoId))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void obtenerComprobante_Existente_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        PagoDTO dto = new PagoDTO();
        when(service.obtenerDetallePagoDTO(id)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/pagos/comprobante/{id}", id))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void obtenerComprobante_NoEncontrado_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.obtenerDetallePagoDTO(id)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/pagos/comprobante/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

}
