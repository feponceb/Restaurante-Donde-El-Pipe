package com.dondeElPipe.gestorPedidos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "estado_pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoPedido {
    
    @Id
    private Integer id;
    private String nombre;
}
