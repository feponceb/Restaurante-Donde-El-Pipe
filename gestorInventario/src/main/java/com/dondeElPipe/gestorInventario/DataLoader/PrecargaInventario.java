package com.dondeElPipe.gestorInventario.DataLoader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorInventario.model.CategoriaInsumo;
import com.dondeElPipe.gestorInventario.model.Inventario;
import com.dondeElPipe.gestorInventario.repository.CategoriaInsumoRepository;
import com.dondeElPipe.gestorInventario.repository.InventarioRepository;

@Configuration
public class PrecargaInventario {

    @Bean
    CommandLineRunner init(InventarioRepository repo, CategoriaInsumoRepository catRepo) {
        return args -> {
            // Evaluamos si ambas tablas están vacías para poblar la BD desde cero
            if (catRepo.count() == 0 && repo.count() == 0) {
                
                // 1. Insertamos las categorías en la BD y recuperamos sus objetos con IDs generados
                CategoriaInsumo carnes = catRepo.save(new CategoriaInsumo(null, "CARNES")); // ID: 1
                CategoriaInsumo liquidos = catRepo.save(new CategoriaInsumo(null, "LÍQUIDOS")); // ID: 2
                CategoriaInsumo verduras = catRepo.save(new CategoriaInsumo(null, "VERDURAS")); // ID: 3
                CategoriaInsumo abarrotes = catRepo.save(new CategoriaInsumo(null, "ABARROTES")); // ID: 4

                // 2. ¡Logrado! El constructor ahora entiende y recibe un número ID directo (.getId())
                repo.save(new Inventario(null, "Carne de Vacuno", 15.5, "KG", carnes.getId()));
                repo.save(new Inventario(null, "Coca Cola 350ml", 48.0, "UNIDADES", liquidos.getId()));
                repo.save(new Inventario(null, "Tomate", 20.0, "KG", verduras.getId()));
                repo.save(new Inventario(null, "Papas fritas congeladas", 30.0, "KG", abarrotes.getId()));
            }
        };
    }

}
