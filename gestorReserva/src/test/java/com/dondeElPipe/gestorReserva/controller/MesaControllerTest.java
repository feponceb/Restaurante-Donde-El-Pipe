package com.dondeElPipe.gestorReserva.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.dondeElPipe.gestorReserva.DTO.MesaSImpleDTO;
import com.dondeElPipe.gestorReserva.model.Mesa;
import com.dondeElPipe.gestorReserva.service.MesaService;

@WebMvcTest(MesaController.class)
public class MesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MesaService service;

    @Test
    void crearMesa_DeberiaRetornarStatus201() throws Exception {
        // Arrange - Adaptado exactamente a los atributos de tu entidad Mesa
        String mesaJson = """
            {
                "capacidadAsientos": 4,
                "estado": 1
            }
            """;
        Mesa nuevaMesa = new Mesa();
        nuevaMesa.setId(1);
        nuevaMesa.setCapacidadAsientos(4);
        nuevaMesa.setEstado(1);
        
        when(service.guardarMesa(any(Mesa.class))).thenReturn(nuevaMesa);

        // Act & Assert
        mockMvc.perform(post("/reserva/mesas/agregar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(mesaJson))
               .andExpect(status().isCreated()); // Ahora sí devolverá 201 Created 🟢
    }

    @Test
    void listar_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        List<Mesa> lista = List.of(new Mesa());
        when(service.listarTodas()).thenReturn(lista);

        // Act & Assert
        mockMvc.perform(get("/reserva/mesas/listar"))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void buscarPorId_Existente_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        Mesa mesa = new Mesa();
        when(service.buscarPorId(id)).thenReturn(Optional.of(mesa));

        // Act & Assert
        mockMvc.perform(get("/reserva/mesas/buscar/{id}", id))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void buscarPorId_NoExistente_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/reserva/mesas/buscar/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void cambiarEstado_Exitoso_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        String bodyJson = """
            {
                "nuevoEstado": 2
            }
            """;
        Mesa mesaActualizada = new Mesa();
        when(service.cambiarEstadoMesa(eq(id), eq(2))).thenReturn(mesaActualizada);

        // Act & Assert
        mockMvc.perform(put("/reserva/mesas/actualizar-estado/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(bodyJson))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void cambiarEstado_CuerpoInvalido_DeberiaRetornarStatus400() throws Exception {
        // Arrange
        Integer id = 1;
        String bodyInvalidoJson = """
            {
                "estadoIncorrecto": 2
            }
            """; // Falta la propiedad 'nuevoEstado' requerida por tu controlador

        // Act & Assert
        mockMvc.perform(put("/reserva/mesas/actualizar-estado/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(bodyInvalidoJson))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void cambiarEstado_NoEncontrado_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        String bodyJson = """
            {
                "nuevoEstado": 2
            }
            """;
        when(service.cambiarEstadoMesa(eq(id), eq(2))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/reserva/mesas/actualizar-estado/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(bodyJson))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void listarDTO_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        List<MesaSImpleDTO> listaDto = List.of(new MesaSImpleDTO());
        when(service.listarTodasDTO()).thenReturn(listaDto);

        // Act & Assert
        mockMvc.perform(get("/reserva/mesas/dto/listar"))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void buscarPorIdDTO_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        MesaSImpleDTO dto = new MesaSImpleDTO();
        when(service.obtenerPorIdDTO(id)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/reserva/mesas/dto/buscar/{id}", id))
               .andExpect(status().isOk()); // 200 OK
    }

}
