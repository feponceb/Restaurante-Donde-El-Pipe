package com.dondeElPipe.gestorDelivery.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorDelivery.DTO.DespachoDTO;
import com.dondeElPipe.gestorDelivery.model.Despacho;
import com.dondeElPipe.gestorDelivery.repository.DespachoRepository;

@Service
public class DespachoService {

    @Autowired
    private DespachoRepository repo;

    // 1. Crear la hoja de despacho (Se gatillará cuando el pedido esté LISTO y PAGADO)
    public DespachoDTO registrarDespachoBase(Integer pedidoId, String direccion) {
        Despacho despacho = new Despacho();
        despacho.setPedidoId(pedidoId);
        despacho.setDireccionEntrega(direccion);
        despacho.setEstadoDelivery("ASIGNADO");

        Despacho guardado = repo.save(despacho);
        return mapearADto(guardado);
    }

    // 2. El repartidor toma el pedido y sale del local
    public DespachoDTO iniciarRuta(Integer id, Integer repartidorId) {
        Optional<Despacho> despOpt = repo.findById(id);
        if (despOpt.isPresent()) {
            Despacho despacho = despOpt.get();
            despacho.setRepartidorId(repartidorId);
            despacho.setEstadoDelivery("EN_CAMINO");
            despacho.setFechaSalida(LocalDateTime.now());
            return mapearADto(repo.save(despacho));
        }
        return null;
    }

    // 3. El repartidor confirma la entrega exitosa
    public DespachoDTO marcarComoEntregado(Integer id) {
        Optional<Despacho> despOpt = repo.findById(id);
        if (despOpt.isPresent()) {
            Despacho despacho = despOpt.get();
            despacho.setEstadoDelivery("ENTREGADO");
            despacho.setFechaEntrega(LocalDateTime.now());
            return mapearADto(repo.save(despacho));
        }
        return null;
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
