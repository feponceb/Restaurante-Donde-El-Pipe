package com.dondeElPipe.gestorInventario.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dondeElPipe.gestorInventario.model.Inventario;
import com.dondeElPipe.gestorInventario.service.InventarioService;

@WebMvcTest(InventarioController.class)
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventarioService service;

    @Test
    void listarTodo_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        List<Inventario> bodega = List.of(new Inventario(1, "Vacuno", 50));
        when(service.obtenerTodo()).thenReturn(bodega);

        // Act & Assert
        mockMvc.perform(get("/inventario/listar"))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void agregarOReabastecer_Exitoso_DeberiaRetornarStatus201() throws Exception {
        // Arrange - JSON que respeta las anotaciones @NotBlank y @NotNull/@Min(0)
        String inventarioJson = """
            {
                "nombreIngrediente": "Pan",
                "stock": 100
            }
            """;
        Inventario itemGuardado = new Inventario(2, "Pan", 100);
        when(service.agregarOReabastecer(any(Inventario.class))).thenReturn(itemGuardado);

        // Act & Assert
        mockMvc.perform(post("/inventario/agregar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(inventarioJson))
               .andExpect(status().isCreated()); // 201 Created
    }

    @Test
    void modificar_Existente_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        String inventarioJson = """
            {
                "nombreIngrediente": "Tomate",
                "stock": 30
            }
            """;
        Inventario itemActualizado = new Inventario(id, "Tomate", 30);
        when(service.modificarProducto(eq(id), any(Inventario.class))).thenReturn(itemActualizado);

        // Act & Assert
        mockMvc.perform(put("/inventario/modificar/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(inventarioJson))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void modificar_NoExistente_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        String inventarioJson = """
            {
                "nombreIngrediente": "Insumo Fantasma",
                "stock": 10
            }
            """;
        // Simulamos la excepción que captura el bloque try-catch del controlador
        when(service.modificarProducto(eq(id), any(Inventario.class)))
               .thenThrow(new RuntimeException("El producto con ID " + id + " no fue encontrado"));

        // Act & Assert
        mockMvc.perform(put("/inventario/modificar/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(inventarioJson))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void descontar_StockSuficiente_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        List<String> ingredientes = List.of("Vacuno", "Pan", "Tomate");
        String ingredientesJson = """
            ["Vacuno", "Pan", "Tomate"]
            """;
        // service.descontarStock es un método void, usamos doNothing() para simular éxito
        doNothing().when(service).descontarStock(ingredientes);

        // Act & Assert
        mockMvc.perform(put("/inventario/descontar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(ingredientesJson))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void descontar_StockInsuficiente_DeberiaRetornarStatus400() throws Exception {
        // Arrange
        List<String> ingredientes = List.of("Vacuno");
        String ingredientesJson = """
            ["Vacuno"]
            """;
        // Simulamos el fallo de stock que captura el try-catch transformándolo en 400 Bad Request
        doThrow(new RuntimeException("Stock insuficiente para el ingrediente: Vacuno"))
               .when(service).descontarStock(ingredientes);

        // Act & Assert
        mockMvc.perform(put("/inventario/descontar")
               .contentType(MediaType.APPLICATION_JSON)
               .content(ingredientesJson))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

}
