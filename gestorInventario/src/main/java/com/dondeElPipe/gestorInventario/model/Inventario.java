package com.dondeElPipe.gestorInventario.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Nombre del insumo (ej: "Carne de Vacuno", "Coca Cola 350ml", "Papas fritas congeladas")
    @NotBlank(message = "El nombre del insumo no puede estar vacío")
    @Column(nullable = false, length = 99, unique = true)
    private String nombreInsumo;

    // Cantidad actual disponible en el restaurante
    @NotNull(message = "Debe registrar un stock inicial")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Double stockActual; // Usamos Double por si manejas kilos flotantes (ej: 1.5 kg)

    // Unidad de medida (ej: "KG", "LTS", "UNIDADES")
    @NotBlank(message = "Debe especificar la unidad de medida")
    @Column(nullable = false, length = 20)
    private String unidadMedida;

    @NotNull(message = "Debe ingresar el ID de la categoría")
    @Column(name = "categoria_id", nullable = false) 
    private Integer categoria;

}
