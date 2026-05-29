package com.dondeElPipe.gestorPedidos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detalle_pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetallePedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Debe ingresar el ID del plato")
    @Column(name = "plato_id", nullable = false)
    private Integer platoId;

    @NotNull(message = "Debe especificar la cantidad")
    @Min(value = 1, message = "La cantidad mínima debe ser 1")
    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double subtotal = 0.0;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonIgnore // Evita bucles infinitos en el JSON
    private Pedido pedido;

}
