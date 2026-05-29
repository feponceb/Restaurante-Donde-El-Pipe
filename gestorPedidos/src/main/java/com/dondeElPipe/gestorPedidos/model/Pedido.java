package com.dondeElPipe.gestorPedidos.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "tipo_pedido_id")
    @NotNull(message = "El tipo de pedido es obligatorio")
    private TipoPedido tipoPedido;

    @ManyToOne
    @JoinColumn(name = "estado_pedido_id")
    private EstadoPedido estadoPedido;

    @NotNull(message = "El ID del usuario/garzón responsable es obligatorio.")
    private Integer usuarioId;

    private Integer mesaId; // Obligatorio solo si tipoPedido == Local

    private LocalDateTime fechaCreacion;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "pedido_id")
    private List<DetallePedido> detalles;

}
