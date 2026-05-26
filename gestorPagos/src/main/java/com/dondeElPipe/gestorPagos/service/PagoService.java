package com.dondeElPipe.gestorPagos.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorPagos.DTO.PagoDTO;
import com.dondeElPipe.gestorPagos.model.Pago;
import com.dondeElPipe.gestorPagos.repository.PagoRepository;

@Service
public class PagoService {

    @Autowired
    private PagoRepository repo;

    // Lógica para registrar y procesar un pago
    public PagoDTO registrarPago(Pago pago) {
        // Asignamos los valores lógicos del negocio directamente en el backend
        pago.setEstado("APROBADO");
        pago.setFechaPago(LocalDateTime.now());

        // Guardamos el objeto en la base de datos
        Pago pagoGuardado = repo.save(pago);

        // Mapeamos el modelo al DTO de salida para cumplir el estándar de arquitectura
        return new PagoDTO(
            pagoGuardado.getId(),
            pagoGuardado.getPedidoId(),
            pagoGuardado.getMonto(),
            pagoGuardado.getMetodoPago(),
            pagoGuardado.getEstado(),
            pagoGuardado.getFechaPago()
        );
    }

    // Lógica para verificar si un pedido específico tiene un pago aprobado
    public Optional<Pago> buscarPagoAprobadoPorPedido(Integer pedidoId) {
        return repo.findByPedidoIdAndEstado(pedidoId, "APROBADO");
    }

}
