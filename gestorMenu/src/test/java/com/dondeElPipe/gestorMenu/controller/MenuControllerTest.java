package com.dondeElPipe.gestorMenu.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import com.dondeElPipe.gestorMenu.DTO.MenuDTO;
import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.service.MenuService;

@WebMvcTest(MenuController.class)
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuService service;

    @Test
    void listar_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        List<Menu> platos = List.of(new Menu());
        when(service.listar()).thenReturn(platos);

        // Act & Assert
        mockMvc.perform(get("/menu/platillos/todo"))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void listarDTO_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        List<MenuDTO> menuDtoList = List.of(new MenuDTO());
        when(service.listarTodoElMenu()).thenReturn(menuDtoList);

        // Act & Assert
        mockMvc.perform(get("/menu/platillos/todoDTO"))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void nuevoPlato_Exitoso_DeberiaRetornarStatus201() throws Exception {
        // Arrange
        // JSON que cumple con las validaciones del modelo (@NotBlank, @Min=4990, @NotNull)
        String menuJson = """
            {
                "nombrePlato": "Lomo a lo Pobre",
                "descripcion": "Exquisito lomo con papas fritas, huevo y cebolla frita",
                "precio": 12990.0,
                "categoria": {
                    "id": 1,
                    "nombre": "Fondos"
                },
                "ingredientes": ["Lomo Liso", "Papas", "Huevo", "Cebolla"]
            }
            """;
        Menu nuevoPlato = new Menu();
        when(service.crearPlato(any(Menu.class))).thenReturn(nuevoPlato);

        // Act & Assert
        mockMvc.perform(post("/menu/platillos/nuevo-plato")
               .contentType(MediaType.APPLICATION_JSON)
               .content(menuJson))
               .andExpect(status().isCreated()); // 201 Created
    }

    @Test
    void nuevoPlato_Duplicado_DeberiaRetornarStatus400() throws Exception {
        // Arrange
        String menuJson = """
            {
                "nombrePlato": "Lomo a lo Pobre",
                "descripcion": "Exquisito lomo con papas fritas, huevo y cebolla frita",
                "precio": 12990.0,
                "categoria": { "id": 1 },
                "ingredientes": ["Lomo Liso"]
            }
            """;
        // Simulamos que el service retorna null cuando el nombre del plato está repetido
        when(service.crearPlato(any(Menu.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/menu/platillos/nuevo-plato")
               .contentType(MediaType.APPLICATION_JSON)
               .content(menuJson))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void eliminarPlato_Existente_DeberiaRetornarStatus204() throws Exception {
        // Arrange
        Integer id = 1;
        Menu platoExistente = new Menu();
        when(service.buscarId(id)).thenReturn(Optional.of(platoExistente));
        doNothing().when(service).eliminarPlato(id);

        // Act & Assert
        mockMvc.perform(delete("/menu/platillos/eliminar-plato/{id}", id))
               .andExpect(status().isNoContent()); // 204 No Content
    }

    @Test
    void eliminarPlato_NoExistente_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.buscarId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/menu/platillos/eliminar-plato/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void actualizarPlato_Existente_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        String menuJson = """
            {
                "nombrePlato": "Lomo a lo Pobre Modificado",
                "descripcion": "Descripción nueva",
                "precio": 13500.0,
                "categoria": { "id": 1 },
                "ingredientes": ["Lomo Liso"]
            }
            """;
        Menu platoExistente = new Menu();
        
        // Indicamos que el plato existe
        when(service.buscarId(id)).thenReturn(Optional.of(platoExistente));
        
        // CORRECCIÓN: Quitamos doNothing() y usamos thenReturn() adaptándonos a que el método devuelve un valor
        when(service.actualizarMenu(eq(id), any(Menu.class))).thenReturn(platoExistente);

        // Act & Assert
        mockMvc.perform(put("/menu/platillos/modificar-plato/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(menuJson))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void actualizarPlato_NoExistente_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        String menuJson = """
            {
                "nombrePlato": "Plato Fantasma",
                "descripcion": "No existe",
                "precio": 5000.0,
                "categoria": { "id": 1 },
                "ingredientes": []
            }
            """;
        when(service.buscarId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/menu/platillos/modificar-plato/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(menuJson))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void obtenerPlatoPorId_Existente_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        Menu plato = new Menu();
        when(service.buscarId(id)).thenReturn(Optional.of(plato));

        // Act & Assert
        mockMvc.perform(get("/menu/platillos/buscar/{id}", id))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void obtenerPlatoPorId_NoExistente_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.buscarId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/menu/platillos/buscar/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

}
