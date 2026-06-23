package com.dondeElPipe.gestorMenu.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dondeElPipe.gestorMenu.DTO.MenuDTO;
import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.service.MenuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/menu/platillos")
public class MenuController {

    //inyeccion de service
    private final MenuService service;

    public MenuController(MenuService service) {
        this.service = service;
    }
    
    //--+----+----+----+----+----+----+----+----+----+----+--
    //--+----+----+----+--Metodos Crud--+----+----+----+----+--
    //--+----+----+----+----+----+----+----+----+----+----+--
    
    // buscar todo
    @Operation(
        summary = "Listar todos los platillos",
        description = "Obtiene una lista con todos los platos registrados en el menú con su modelo de datos completo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de platos obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/todo")
    public ResponseEntity<List<Menu>> listar() {
        List<Menu> platos = service.listar();
        return ResponseEntity.ok(platos);
    }

    // buscar todo en formato DTO
    @Operation(
        summary = "Listar menú completo (DTO)",
        description = "Obtiene la lista de platillos en formato simplificado optimizado para la vista"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista en formato DTO obtenida correctamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/todoDTO")
    public ResponseEntity<List<MenuDTO>> listarDTO() {
        List<MenuDTO> menu = service.listarTodoElMenu();
        return ResponseEntity.ok(menu);
    }
    
    // crear un plato Response
    @Operation(
        summary = "Registrar un nuevo plato",
        description = "Agrega un nuevo platillo al catálogo validando que no exista un nombre idéntico duplicado"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Platillo creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (El nombre del plato ya existe en el sistema)"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/nuevo-plato")
    public ResponseEntity<?> nuevoPlato(@Valid @RequestBody Menu menu){
        Menu nuevo = service.crearPlato(menu);

        if (nuevo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Error: El nombre del plato '" + menu.getNombrePlato() + "' ya existe.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }
    
    // eliminar un plato por id
    @Operation(
        summary = "Eliminar platillo por ID",
        description = "Remueve permanentemente un platillo del menú a través de su ID único"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Platillo eliminado correctamente (Sin cuerpo de respuesta)"),
        @ApiResponse(responseCode = "404", description = "El platillo con el ID especificado no fue encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/eliminar-plato/{id}")
    public ResponseEntity<?> eliminarPlato(@PathVariable Integer id) {
        Optional<Menu> menu = service.buscarId(id);

        if (menu.isPresent()) {
            service.eliminarPlato(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();                                   
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("El plato " + id + " no fue encontrado");
        }
    }
    
    // actualizar un plato
    @Operation(
        summary = "Actualizar platillo por ID",
        description = "Modifica los componentes o características de un plato existente mediante su ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Platillo actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "El platillo solicitado para modificar no existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/modificar-plato/{id}")
    public ResponseEntity<?> actualizarPlato(@Valid @PathVariable Integer id, @RequestBody Menu menu){
        Optional<Menu> existente = service.buscarId(id);

        if (existente.isPresent()) {
            service.actualizarMenu(id, menu);
            return ResponseEntity.status(HttpStatus.OK)
                                    .body("Plato modificado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("El plato " + id + " no fue encontrado");
        }
    }

    // buscar por id
    @Operation(
        summary = "Buscar platillo por ID",
        description = "Recupera la información completa de un platillo específico usando su identificador"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Platillo localizado con éxito"),
        @ApiResponse(responseCode = "404", description = "Platillo no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/buscar/{id}")
    public ResponseEntity<?> obtenerPlatoPorId(@PathVariable Integer id) {
        Optional<Menu> plato = service.buscarId(id);
        
        if (plato.isPresent()) {
            return ResponseEntity.ok(plato.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Plato no encontrado");
        }
    }

}

