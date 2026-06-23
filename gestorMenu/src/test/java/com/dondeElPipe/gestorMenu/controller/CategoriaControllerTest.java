package com.dondeElPipe.gestorMenu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dondeElPipe.gestorMenu.model.CategoriaMenu;
import com.dondeElPipe.gestorMenu.service.CategoriaService;

@WebMvcTest(CategoriaController.class)
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaService service;

    @Test
    void obtenerCategorias_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        List<CategoriaMenu> categorias = List.of(new CategoriaMenu(1, "Bebestibles"));
        when(service.listarTodo()).thenReturn(categorias);

        // Act & Assert
        mockMvc.perform(get("/menu/categoria/todo"))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void crearCategoria_Exitoso_DeberiaRetornarStatus201() throws Exception {
        // Arrange
        String categoriaJson = """
            {
                "nombre": "Entradas"
            }
            """;
        CategoriaMenu nuevaCategoria = new CategoriaMenu(2, "Entradas");
        when(service.crear(any(CategoriaMenu.class))).thenReturn(nuevaCategoria);

        // Act & Assert
        mockMvc.perform(post("/menu/categoria/nueva")
               .contentType(MediaType.APPLICATION_JSON)
               .content(categoriaJson))
               .andExpect(status().isCreated()); // 201 Created
    }

    @Test
    void crearCategoria_Duplicada_DeberiaRetornarStatus400() throws Exception {
        // Arrange
        String categoriaJson = """
            {
                "nombre": "Fondos"
            }
            """;
        // Tu controlador maneja que si la categoría ya existe, service.crear devuelve null
        when(service.crear(any(CategoriaMenu.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/menu/categoria/nueva")
               .contentType(MediaType.APPLICATION_JSON)
               .content(categoriaJson))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void eliminarCategoria_Existente_DeberiaRetornarStatus204() throws Exception {
        // Arrange
        Integer id = 1;
        CategoriaMenu categoriaExistente = new CategoriaMenu(id, "Postres");
        
        when(service.buscarPorId(id)).thenReturn(Optional.of(categoriaExistente));
        doNothing().when(service).eliminar(id); // service.eliminar es un método void

        // Act & Assert
        mockMvc.perform(delete("/menu/categoria/eliminar/{id}", id))
               .andExpect(status().isNoContent()); // 204 No Content
    }

    @Test
    void eliminarCategoria_NoExistente_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/menu/categoria/eliminar/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

}
