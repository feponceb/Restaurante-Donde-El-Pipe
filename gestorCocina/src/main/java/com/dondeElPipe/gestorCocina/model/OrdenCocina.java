package com.dondeElPipe.gestorCocina.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ordenes_cocina")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenCocina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @Column(nullable = false, length = 30)
    private String estadoPreparation; // "PENDIENTE", "EN_PREPARACION", "LISTO"

    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

}
