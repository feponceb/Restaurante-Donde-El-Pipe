package com.dondeElPipe.gestorCocina.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orden_cocina")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdenCocina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El ID del pedido es obligatorio")
    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @NotNull(message = "El estado de la cocina es obligatorio")
    @Column(name = "estado_cocina", nullable = false, length = 30) // "EN_ESPERA", "PREPARANDO", "LISTO"
    private Integer estadoCocina;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

}
