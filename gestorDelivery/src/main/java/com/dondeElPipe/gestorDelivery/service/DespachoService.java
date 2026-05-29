package com.dondeElPipe.gestorDelivery.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorDelivery.DTO.DespachoDTO;
import com.dondeElPipe.gestorDelivery.model.Despacho;
import com.dondeElPipe.gestorDelivery.repository.DespachoRepository;

@Service
public class DespachoService {

    @Autowired
    private DespachoRepository repo;

    // ========================================================
    // FUNCIONES NORMALES (Retornan Entidad Cruda 'Despacho')
    // ========================================================

    // 1. Crear la hoja de despacho inicial (Mutación Normal)
    public Despacho registrarDespachoBase(Integer pedidoId, String direccion) {
        if (pedidoId == null || direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("El ID de pedido y la dirección de entrega son datos obligatorios.");
        }
        
        Despacho despacho = new Despacho();
        despacho.setPedidoId(pedidoId);
        despacho.setDireccionEntrega(direccion);
        despacho.setEstadoDelivery("ASIGNADO");

        return repo.save(despacho);
    }

    // 2. El repartidor toma el pedido y sale del local (Mutación Normal)
    public Despacho iniciarRuta(Integer id, Integer repartidorId) {
        if (repartidorId == null) {
            throw new IllegalArgumentException("Debe especificar un ID de repartidor válido.");
        }

        Despacho despacho = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El registro de despacho con ID " + id + " no existe."));
            
        despacho.setRepartidorId(repartidorId);
        despacho.setEstadoDelivery("EN_CAMINO");
        despacho.setFechaSalida(LocalDateTime.now());
        
        return repo.save(despacho);
    }

    // 3. El repartidor confirma la entrega exitosa (Mutación Normal)
    public Despacho marcarComoEntregado(Integer id) {
        Despacho despacho = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El registro de despacho con ID " + id + " no existe."));
            
        despacho.setEstadoDelivery("ENTREGADO");
        despacho.setFechaEntrega(LocalDateTime.now());
        
        return repo.save(despacho);
    }

    // ========================================================
    // FUNCIONES DTO (Retornan Salidas Simplificadas para Lectura)
    // ========================================================

    // 4. Buscar historial de despacho por ID de Pedido (Consulta DTO)
    public DespachoDTO obtenerPorPedidoIdDTO(Integer pedidoId) {
        Despacho despacho = repo.findByPedidoId(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró ningún despacho asociado al pedido ID: " + pedidoId));
        return mapearADto(despacho);
    }

    // 5. Listar todos los despachos del restaurante (Consulta DTO)
    public List<DespachoDTO> listarTodosDTO() {
        return repo.findAll().stream().map(this::mapearADto).toList();
    }

    // Función auxiliar para transformar el modelo a DTO
    private DespachoDTO mapearADto(Despacho d) {
        return new DespachoDTO(
            d.getId(),
            d.getPedidoId(),
            d.getDireccionEntrega(),
            d.getRepartidorId(),
            d.getEstadoDelivery(),
            d.getFechaSalida(),
            d.getFechaEntrega()
        );
    }

}
