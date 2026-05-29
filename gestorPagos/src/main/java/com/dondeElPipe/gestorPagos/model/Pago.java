package com.dondeElPipe.gestorPagos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pago")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El ID del pedido es obligatorio")
    @Column(name = "pedido_id", nullable = false)
    private Integer pedidoId;

    @NotNull(message = "El monto del pago es obligatorio")
    @DecimalMin(value = "1.0", message = "El monto debe ser mayor a cero")
    @Column(nullable = false)
    private Double monto;

    @NotBlank(message = "El método de pago es obligatorio")
    @Column(name = "metodo_pago", nullable = false, length = 30) // Ej: "EFECTIVO", "TARJETA", "TRANSFERENCIA"
    private String metodoPago;

    //@NotBlank(message = "El estado del pago es obligatorio")
    @Column(nullable = false, length = 20) // "PENDIENTE", "APROBADO", "RECHAZADO"
    private String estado;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

}
