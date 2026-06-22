package com.dondeElPipe.gestorInventario.DataLoader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorInventario.model.Inventario;
import com.dondeElPipe.gestorInventario.repository.InventarioRepository;

@Configuration
public class PrecargaInventario {
    @Bean
    CommandLineRunner initBodega(InventarioRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                // Insumos Cazuela y Mariscal
                repo.save(new Inventario(null, "Vacuno", 50));
                repo.save(new Inventario(null, "Zapallo", 30));
                repo.save(new Inventario(null, "Choclo", 40));
                repo.save(new Inventario(null, "Papa", 100));
                repo.save(new Inventario(null, "Arroz", 80));
                repo.save(new Inventario(null, "Choros", 25));
                repo.save(new Inventario(null, "Almejas", 25));

                // Insumos Completos
                repo.save(new Inventario(null, "Pan", 200));
                repo.save(new Inventario(null, "Vienesa", 150));
                repo.save(new Inventario(null, "Tomate", 90));
                repo.save(new Inventario(null, "Palta", 60));
                repo.save(new Inventario(null, "Mayonesa", 120));
                
                System.out.println(">>> Bodega precargada de forma exitosa en Puerto 8081.");
            }
        };
    }

}
