package com.dondeElPipe.gestorDelivery.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "despacho")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Despacho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El ID del pedido es obligatorio")
    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Column(name = "direccion_entrega", nullable = false, length = 150)
    private String direccionEntrega;

    @Column(name = "repartidor_id") // Puede iniciar null hasta que se asigne un repartidor
    private Integer repartidorId;

    @NotBlank(message = "El estado del despacho es obligatorio")
    @Column(name = "estado_delivery", nullable = false, length = 30) // "ASIGNADO", "EN_CAMINO", "ENTREGADO"
    private String estadoDelivery;

    @Column(name = "fecha_salida")
    private LocalDateTime fechaSalida;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

}
