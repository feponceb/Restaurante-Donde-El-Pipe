package com.dondeElPipe.gestorReserva.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorReserva.DTO.MesaSImpleDTO;
import com.dondeElPipe.gestorReserva.model.EstadoMesa;
import com.dondeElPipe.gestorReserva.model.Mesa;
import com.dondeElPipe.gestorReserva.repository.EstadoMesaRepository;
import com.dondeElPipe.gestorReserva.repository.MesaRepository;

@Service
public class MesaService {

    @Autowired
    private MesaRepository repo;

    @Autowired
    private EstadoMesaRepository estadoMesaRepo;

    // 1. GUARDAR MESA
    public Mesa guardarMesa(Mesa mesa) {
        // Forzamos que al crearse, inicie con el ID 1 (que corresponde a "Habilitada" en la BD)
        mesa.setEstado(1);
        return repo.save(mesa);
    }

    // 2. LISTAR MESAS
    public List<Mesa> listarTodas() {
        return repo.findAll();
    }

    // 3. BUSCAR POR ID
    public Optional<Mesa> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    // 4. CAMBIAR ESTADO DE LA MESA
    public Mesa cambiarEstadoMesa(Integer id, Integer nuevoEstado) {
        Optional<Mesa> mesaOpt = repo.findById(id);
        
        // Validación de presencia y verificación de que el ID del estado exista
        if (mesaOpt.isEmpty() || !estadoMesaRepo.existsById(nuevoEstado)) {
            return null;
        }
        
        Mesa mesa = mesaOpt.get();
        mesa.setEstado(nuevoEstado);
        return repo.save(mesa);
    }

    // ==========================================
    // NUEVAS FUNCIONES DTO (CON MANEJO DE ERRORES)
    // ==========================================

    public List<MesaSImpleDTO> listarTodasDTO() {
        List<Mesa> mesas = repo.findAll();
        List<MesaSImpleDTO> listaDTO = new ArrayList<>();
        for (Mesa m : mesas) {
            listaDTO.add(convertirADTO(m));
        }
        return listaDTO;
    }

    public MesaSImpleDTO obtenerPorIdDTO(Integer id) {
        Optional<Mesa> mesaOpt = repo.findById(id);
        if (mesaOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró la mesa con el ID: " + id);
        }
        return convertirADTO(mesaOpt.get());
    }

    // Método de mapeo manual auxiliar tradicional
    private MesaSImpleDTO convertirADTO(Mesa mesa) {
        // CORRECCIÓN: Usamos una expresión lambda (e -> e.getNombre()) en lugar de EstadoMesa::getNombre
        String nombreEstado = estadoMesaRepo.findById(mesa.getEstado())
                                            .map(e -> e.getNombre())
                                            .orElse("DESCONOCIDO");

        MesaSImpleDTO dto = new MesaSImpleDTO();
        dto.setIdMesa(mesa.getId());
        dto.setCapacidadAsientos(mesa.getCapacidadAsientos());
        dto.setEstadoMesa(nombreEstado); // Asigna el String (ej: "Habilitada") al DTO
        return dto;
    }

}
