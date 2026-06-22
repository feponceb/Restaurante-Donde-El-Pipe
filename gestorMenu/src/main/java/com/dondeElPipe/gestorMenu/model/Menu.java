package com.dondeElPipe.gestorMenu.model;



import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "menu")
@AllArgsConstructor
@NoArgsConstructor
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Debe de tener un nombre")
    @Column(nullable = false, length = 99, unique = true)
    private String nombrePlato;

    @NotBlank(message = "Debe de tener una descripción")
    @Column(nullable = false, length = 255)
    private String descripcion;

    @NotNull(message = "Debe de tener un precio")
    @Column(nullable = false)
    @Min(value = 4990, message = "Debe de tener un precio de almenos 4,990")
    private Double precio;

    //Deberia tener una tabla externa donde se puedan crear las categorias
    //y ser llamadas como ingredientes
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    @NotNull(message = "Debe de tener una categoría")
    private CategoriaMenu categoria;

    // Tabla secundaria automatizada para almacenar la lista de ingredientes simples
    @ElementCollection
    @CollectionTable(name = "menu_ingredientes", joinColumns = @JoinColumn(name = "menu_id"))
    @Column(name = "ingrediente", nullable = false)
    private List<String> ingredientes;

}
