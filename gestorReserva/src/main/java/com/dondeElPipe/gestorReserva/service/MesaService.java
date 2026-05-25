package com.dondeElPipe.gestorReserva.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorReserva.model.EstadoMesa;
import com.dondeElPipe.gestorReserva.model.Mesa;
import com.dondeElPipe.gestorReserva.repository.MesaRepository;

@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepo;

    // Listar todas las mesas (Ideal para ver el mapa del comedor)
    public List<Mesa> listarTodas() {
        return mesaRepo.findAll();
    }

    // Buscar una mesa específica por su ID (El método que llama gestorPedidos)
    public Optional<Mesa> buscarPorId(Integer id) {
        return mesaRepo.findById(id);
    }

    // Modificar el estado de una mesa (Para pasarla a Ocupada, Reservada, etc.)
    public Mesa cambiarEstadoMesa(Integer id, EstadoMesa nuevoEstado) {
        Optional<Mesa> mesaOpt = mesaRepo.findById(id);
        
        if (mesaOpt.isPresent()) {
            Mesa mesa = mesaOpt.get();
            mesa.setEstado(nuevoEstado);
            return mesaRepo.save(mesa);
        }
        return null; // Si la mesa no existe
    }

}
