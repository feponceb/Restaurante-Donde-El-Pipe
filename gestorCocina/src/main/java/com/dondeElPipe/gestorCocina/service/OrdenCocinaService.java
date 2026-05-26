package com.dondeElPipe.gestorCocina.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorCocina.DTO.OrdenCocinaDTO;
import com.dondeElPipe.gestorCocina.model.OrdenCocina;
import com.dondeElPipe.gestorCocina.repository.OrdenCocinaRepository;

@Service
public class OrdenCocinaService {

    @Autowired
    private OrdenCocinaRepository repo;

    // 1. Recibir un pedido pagado y ponerlo "EN_ESPERA" en la cola de la cocina
    public OrdenCocinaDTO recibirPedidoEnCocina(Integer pedidoId) {
        OrdenCocina orden = new OrdenCocina();
        orden.setPedidoId(pedidoId);
        orden.setEstadoCocina("EN_ESPERA");
        orden.setFechaIngreso(LocalDateTime.now());

        OrdenCocina guardada = repo.save(orden);
        return mapearADto(guardada);
    }

    // 2. Cambiar el estado (Ej: de "EN_ESPERA" a "PREPARANDO" o "LISTO")
    public OrdenCocinaDTO actualizarEstado(Integer id, String nuevoEstado) {
        Optional<OrdenCocina> ordenOpt = repo.findById(id);
        
        if (ordenOpt.isPresent()) {
            OrdenCocina orden = ordenOpt.get();
            orden.setEstadoCocina(nuevoEstado.toUpperCase().trim());
            return mapearADto(repo.save(orden));
        }
        return null;
    }

    // 3. Listar solo las órdenes que los cocineros tienen activas en la pantalla
    public List<OrdenCocinaDTO> listarOrdenesActivas() {
        List<OrdenCocina> activas = repo.findByEstadoCocinaIn(Arrays.asList("EN_ESPERA", "PREPARANDO"));
        return activas.stream().map(this::mapearADto).toList();
    }

    // Función auxiliar de mapeo para evitar repetir código
    private OrdenCocinaDTO mapearADto(OrdenCocina orden) {
        return new OrdenCocinaDTO(
            orden.getId(),
            orden.getPedidoId(),
            orden.getEstadoCocina(),
            orden.getFechaIngreso()
        );
    }

}
