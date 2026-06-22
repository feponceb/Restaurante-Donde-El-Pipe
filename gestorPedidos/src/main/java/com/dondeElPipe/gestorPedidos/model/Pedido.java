package com.dondeElPipe.gestorPedidos.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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

    @NotNull(message = "Debe asignar una mesa al pedido")
    @Column(name = "id_mesa", nullable = false)
    private Integer idMesa;

    @NotNull(message = "Debe asignar un garzón al pedido")
    @Column(name = "id_garzon", nullable = false)
    private Integer idGarzon;

    @Column(name = "estado_pedido", nullable = false, length = 30)
    private String estadoPedido; // "PENDIENTE_PAGO", "PAGADO", "ENTREGADO", etc.

    @Column(name = "total_pagar", nullable = false)
    private Double totalPagar;

    // Guardamos los IDs de los platillos del menú tal como el ejemplo de las PPTs
    @ElementCollection
    @CollectionTable(name = "pedido_platillos", joinColumns = @JoinColumn(name = "pedido_id"))
    @Column(name = "id_platillo")
    private List<Integer> platillosIds;

}
