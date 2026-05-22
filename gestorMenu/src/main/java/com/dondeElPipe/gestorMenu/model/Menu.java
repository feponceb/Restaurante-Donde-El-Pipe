package com.dondeElPipe.gestorMenu.model;


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

    @NotNull(message = "Debe de tener un precio")
    @Column(nullable = false)
    @Min(value = 4990, message = "Debe de tener un precio de almenos 4,990")
    private Double precio;

    //Deberia tener una tabla externa donde se puedan crear las categorias
    //y ser llamadas como ingredientes
    @NotNull(message = "Debe de tener una categoría")
    @Column(nullable = false, name = "categoria_id")
    private Integer categoria;

}
