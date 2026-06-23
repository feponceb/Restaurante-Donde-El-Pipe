package com.dondeElPipe.gestorCocina.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dondeElPipe.gestorCocina.model.OrdenCocina;
import com.dondeElPipe.gestorCocina.service.CocinaService;

@WebMvcTest(CocinaController.class)
public class CocinaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CocinaService service;

    @Test
    void recibirPedido_Exitoso_DeberiaRetornarStatus201() throws Exception {
        // Arrange
        String bodyJson = """
            {
                "pedidoId": 10
            }
            """;
        OrdenCocina ordenMock = new OrdenCocina(1, 10, "PENDIENTE", LocalDateTime.now());
        when(service.recibirNuevaOrden(10)).thenReturn(ordenMock);

        // Act & Assert
        mockMvc.perform(post("/cocina/recibir-pedido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(bodyJson))
               .andExpect(status().isCreated()); // 201 Created
    }

    @Test
    void recibirPedido_FaltaPedidoId_DeberiaRetornarStatus400() throws Exception {
        // Arrange - JSON sin el campo obligatorio 'pedidoId'
        String bodyJson = """
            {
                "otraPropiedad": "valor"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/cocina/recibir-pedido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(bodyJson))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void recibirPedido_FallaServicio_DeberiaRetornarStatus500() throws Exception {
        // Arrange
        String bodyJson = """
            {
                "pedidoId": 10
            }
            """;
        when(service.recibirNuevaOrden(anyInt())).thenThrow(new RuntimeException("Error en base de datos al obtener el pedido"));

        // Act & Assert
        mockMvc.perform(post("/cocina/recibir-pedido")
               .contentType(MediaType.APPLICATION_JSON)
               .content(bodyJson))
               .andExpect(status().isInternalServerError()); // 500 Internal Server Error
    }

    @Test
    void iniciarPreparacion_Exitoso_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        OrdenCocina ordenActualizada = new OrdenCocina(id, 10, "EN_PREPARACION", LocalDateTime.now());
        when(service.marcarEnPreparacion(id)).thenReturn(ordenActualizada);

        // Act & Assert
        mockMvc.perform(put("/cocina/comandas/{id}/iniciar", id))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void iniciarPreparacion_ErrorEstado_DeberiaRetornarStatus400() throws Exception {
        // Arrange
        Integer id = 1;
        when(service.marcarEnPreparacion(id)).thenThrow(new RuntimeException("La orden ya se encuentra en preparación o lista"));

        // Act & Assert
        mockMvc.perform(put("/cocina/comandas/{id}/iniciar", id))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void terminarPreparacion_Exitoso_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        OrdenCocina ordenListada = new OrdenCocina(id, 10, "LISTO", LocalDateTime.now());
        when(service.marcarComoListo(id)).thenReturn(ordenListada);

        // Act & Assert
        mockMvc.perform(put("/cocina/comandas/{id}/terminar", id))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void terminarPreparacion_QuiebreStock_DeberiaRetornarStatus409() throws Exception {
        // Arrange
        Integer id = 1;
        // Tu controlador maneja excepciones aquí transformándolas en un 409 Conflict (ej: fallas en Inventario)
        when(service.marcarComoListo(id)).thenThrow(new RuntimeException("Quiebre de stock: Insuficiente stock para ingrediente Vacuno"));

        // Act & Assert
        mockMvc.perform(put("/cocina/comandas/{id}/terminar", id))
               .andExpect(status().isConflict()); // 409 Conflict
    }

}
