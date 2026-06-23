package com.dondeElPipe.gestorUsuario.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dondeElPipe.gestorUsuario.DTO.UsuarioDTO;
import com.dondeElPipe.gestorUsuario.model.Usuario;
import com.dondeElPipe.gestorUsuario.service.UsuarioService;

@WebMvcTest(UsuarioController.class)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService service;

    @Test
    void listarUsuarios_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        UsuarioDTO usuarioDTO = new UsuarioDTO(); // Ajusta los setters según tus campos de UsuarioDTO
        List<UsuarioDTO> lista = List.of(usuarioDTO);
        when(service.listar()).thenReturn(lista);

        // Act & Assert
        mockMvc.perform(get("/usuarios/listar")
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk()); // 200 OK
    }

    @Test
    void crearUsuario_Exitoso_DeberiaRetornarStatus201() throws Exception {
        // Arrange - Cambiamos "idRol" por "rol" para cumplir con la validación de tu modelo
        String usuarioJson = """
            {
                "rut": "20909720-6",
                "nombre": "Juan",
                "apellido": "Gomez",
                "rol": 3,
                "email": "juan@dondeelpipe.com",
                "password": "password123"
            }
            """;
        UsuarioDTO nuevoDTO = new UsuarioDTO();
        when(service.crearUsuario(any(Usuario.class))).thenReturn(nuevoDTO);

        // Act & Assert
        mockMvc.perform(post("/usuarios/nuevo-usuario")
               .contentType(MediaType.APPLICATION_JSON)
               .content(usuarioJson))
               .andExpect(status().isCreated()); // Espera 201 Created
    }

    @Test
    void crearUsuario_Invalido_DeberiaRetornarStatus400() throws Exception {
        // Arrange - Completamos todos los campos obligatorios para que pase @Valid, pero simulamos falla de negocio
        String usuarioJson = """
            {
                "rut": "11111111-1",
                "nombre": "Invalido",
                "apellido": "Test",
                "rol": 2,
                "email": "invalido@dondeelpipe.com",
                "password": "password123"
            }
            """;
        when(service.crearUsuario(any(Usuario.class))).thenReturn(null);

        // Act & Assert
        mockMvc.perform(post("/usuarios/nuevo-usuario")
               .contentType(MediaType.APPLICATION_JSON)
               .content(usuarioJson))
               .andExpect(status().isBadRequest()); // Espera 400 Bad Request
    }

    @Test
    void eliminarUsuario_Existente_DeberiaRetornarStatus204() throws Exception {
        // Arrange
        Integer id = 1;
        UsuarioDTO existente = new UsuarioDTO();
        when(service.buscarId(id)).thenReturn(Optional.of(existente));
        doNothing().when(service).eliminarUsuario(id);

        // Act & Assert
        mockMvc.perform(delete("/usuarios/eliminar-usuario/{id}", id))
               .andExpect(status().isNoContent()); // 204 No Content
    }

    @Test
    void eliminarUsuario_NoEncontrado_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.buscarId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/usuarios/eliminar-usuario/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

    @Test
    void actualizarUsuario_Exitoso_DeberiaRetornarStatus200() throws Exception {
        // Arrange
        Integer id = 1;
        String usuarioJson = """
            {
                "rut": "20909720-6",
                "nombre": "Juan Modificado",
                "apellido": "Gomez",
                "rol": 3,
                "email": "juan@dondeelpipe.com",
                "password": "password123"
            }
            """;
        UsuarioDTO existente = new UsuarioDTO();
        Usuario modificado = new Usuario();

        when(service.buscarId(id)).thenReturn(Optional.of(existente));
        when(service.actualizarUsuario(eq(id), any(Usuario.class))).thenReturn(modificado);

        // Act & Assert
        mockMvc.perform(put("/usuarios/modificar-usuario/{id}", id)
               .contentType(MediaType.APPLICATION_JSON)
               .content(usuarioJson))
               .andExpect(status().isOk()); // Espera 200 OK
    }

    @Test
    void obtenerUsuarioPorId_NoEncontrado_DeberiaRetornarStatus404() throws Exception {
        // Arrange
        Integer id = 99;
        when(service.buscarId(id)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/usuarios/buscar/{id}", id))
               .andExpect(status().isNotFound()); // 404 Not Found
    }

}
