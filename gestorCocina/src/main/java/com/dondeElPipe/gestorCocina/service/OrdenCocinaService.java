package com.dondeElPipe.gestorCocina.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorCocina.DTO.OrdenCocinaDTO;
import com.dondeElPipe.gestorCocina.model.EstadoCocina;
import com.dondeElPipe.gestorCocina.model.OrdenCocina;
import com.dondeElPipe.gestorCocina.repository.OrdenCocinaRepository;

@Service
public class OrdenCocinaService {

    @Autowired
    private OrdenCocinaRepository repo;

    // CAMBIO CRÍTICO: Inyectamos el servicio especializado, no el repositorio ajeno
    @Autowired
    private EstadoCocinaService estadoService; 

    // ========================================================
    // FUNCIONES NORMALES (Retornan Entidad Cruda 'OrdenCocina')
    // ========================================================

    // 1. RECIBIR PEDIDO DESDE EL GESTOR DE PEDIDOS (Escritura Normal)
    public OrdenCocina recibirPedidoEnCocina(Integer pedidoId) {
        EstadoCocina estado = estadoService.buscarPorNombre("EN_ESPERA");
        OrdenCocina orden = new OrdenCocina(null, pedidoId, estado.getId(), LocalDateTime.now());
        return repo.save(orden);
    }

    // 2. ACTUALIZAR ESTADO DE LA ORDEN (Mutación Normal)
    public OrdenCocina actualizarEstado(Integer id, Integer nuevoEstadoId) {
        estadoService.buscarPorId(nuevoEstadoId); // Valida si el estado existe

        OrdenCocina orden = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("La orden de cocina con ID " + id + " no existe."));

        orden.setEstadoCocina(nuevoEstadoId);
        return repo.save(orden);
    }

    // ========================================================
    // FUNCIONES DTO (Retornan Salidas Simplificadas para Lectura)
    // ========================================================

    // 3. PANTALLA MONITOR DE ORDENES ACTIVAS (Consulta DTO)
    public List<OrdenCocinaDTO> listarOrdenesActivas() {
        Integer idEnEspera = estadoService.buscarPorNombre("EN_ESPERA").getId();
        Integer idPreparando = estadoService.buscarPorNombre("PREPARANDO").getId();

        List<OrdenCocina> activas = repo.findByEstadoCocinaIn(List.of(idEnEspera, idPreparando));
        return activas.stream().map(this::mapearADto).toList();
    }

    // Método auxiliar de mapeo Entidad -> DTO
    private OrdenCocinaDTO mapearADto(OrdenCocina orden) {
        String nombreEstado = estadoService.buscarPorId(orden.getEstadoCocina()).getNombre();
        return new OrdenCocinaDTO(
            orden.getId(),
            orden.getPedidoId(),
            nombreEstado,
            orden.getFechaIngreso()
        );
    }

}
