package com.dondeElPipe.gestorReserva.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorReserva.DTO.MesaSImpleDTO;
import com.dondeElPipe.gestorReserva.model.EstadoMesa;
import com.dondeElPipe.gestorReserva.model.Mesa;
import com.dondeElPipe.gestorReserva.repository.MesaRepository;

@Service
public class MesaService {

    private final MesaRepository repo;

    public MesaService(MesaRepository repo) {
        this.repo = repo;
    }

    // 1. GUARDAR MESA
    public Mesa guardarMesa(Mesa mesa) {
        // Forzamos que al crearse, la mesa inicie siempre como Habilitada
        mesa.setEstado(EstadoMesa.Habilitada);
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
    public Mesa cambiarEstadoMesa(Integer id, EstadoMesa nuevoEstado) {
        Optional<Mesa> mesaOpt = repo.findById(id);
        
        // Validación de presencia usando isEmpty() como en tus otros servicios
        if (mesaOpt.isEmpty()) {
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
        MesaSImpleDTO dto = new MesaSImpleDTO();
        dto.setIdMesa(mesa.getId());
        dto.setCapacidadAsientos(mesa.getCapacidadAsientos());
        dto.setEstadoMesa(mesa.getEstado().name());
        return dto;
    }

}
