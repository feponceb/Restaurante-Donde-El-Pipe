package com.dondeElPipe.gestorUsuario.controller;

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

import com.dondeElPipe.gestorUsuario.model.Rol;
import com.dondeElPipe.gestorUsuario.service.RolService;

@WebMvcTest(RolController.class)
public class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolService service;

    @Test
    void obtenerRoles_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Rol rol = new Rol();
        rol.setId(1);
        rol.setNombre("ADMIN");
        List<Rol> lista = List.of(rol);
        
        when(service.listarTodo()).thenReturn(lista);

        // Act & Assert
        mockMvc.perform(get("/usuario/roles/todo")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void crearRol_Exitoso_DeberiaRetornarStatus201() throws Exception {
        // Arrange
        String rolJson = """
            {
                "nombre": "GARZON"
            }
            """;
        Rol nuevoRol = new Rol();
        nuevoRol.setId(2);
        nuevoRol.setNombre("GARZON");
        
        when(service.crear(any(Rol.class))).thenReturn(nuevoRol);

        // Act & Assert
        mockMvc.perform(post("/usuario/roles/nuevo")
               .contentType(MediaType.APPLICATION_JSON)
               .content(rolJson))
               .andExpect(status().isCreated()); // 201 Created
    }

    @Test
    void crearRol_Duplicado_DeberiaRetornarStatus400() throws Exception {
        // Arrange
        String rolJson = """
            {
                "nombre": "ADMIN"
            }
            """;
        // Tu controlador maneja que si el rol ya existe, service.crear devuelve null
        when(service.crear(any(Rol.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/usuario/roles/nuevo")
               .contentType(MediaType.APPLICATION_JSON)
               .content(rolJson))
               .andExpect(status().isBadRequest()); // 400 Bad Request
    }

    @Test
    void eliminarRol_Existente_DeberiaRetornarStatus204() throws Exception {
        // Arrange
        Integer id = 1;
        Rol rolExistente = new Rol();
        rolExistente.setId(id);
        rolExistente.setNombre("ADMIN");
        
        when(service.buscarPorId(id)).thenReturn(Optional.of(rolExistente));
        doNothing().when(service).eliminar(id);

        // Act & Assert
        mockMvc.perform(delete("/usuario/roles/eliminar/{id}", id))
               .andExpect(status().isNoContent()); // 204 No Content
    }

    @Test
    void eliminarRol_NoEncontrado_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.buscarPorId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/usuario/roles/eliminar/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

}
