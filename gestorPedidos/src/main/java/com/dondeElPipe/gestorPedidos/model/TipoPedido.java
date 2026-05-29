package com.dondeElPipe.gestorPedidos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_pedido")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TipoPedido {
    
    @Id
    private Integer id;

    private String nombre;

}
