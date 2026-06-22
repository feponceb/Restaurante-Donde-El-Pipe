package com.dondeElPipe.gestorMenu.DataLoader;


import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorMenu.model.CategoriaMenu;
import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.repository.CategoriaMenuRepository;
import com.dondeElPipe.gestorMenu.repository.MenuRepository;

@Configuration
public class PrecargaMenus {
    
    @Bean
    CommandLineRunner init(MenuRepository repo, CategoriaMenuRepository catRepo){
        return args -> {
            // 1. Crear y guardar las Categorías primero
                CategoriaMenu sopas = catRepo.save(new CategoriaMenu(null, "SOPAS"));
                CategoriaMenu completos = catRepo.save(new CategoriaMenu(null, "COMPLETOS"));
                CategoriaMenu asados = catRepo.save(new CategoriaMenu(null, "ASADOS"));

                // 2. Precarga de SOPAS
                repo.save(new Menu(null, 
                    "Cazuela", 
                    "Plato tradicional chileno con carne de vacuno, zapallo, choclo y papas.", 
                    8990.0, 
                    sopas, 
                    Arrays.asList("Vacuno", "Zapallo", "Choclo", "Papa", "Arroz")));

                repo.save(new Menu(null, 
                    "Mariscal Caliente", 
                    "Sopa de mariscos surtidos servida en paila de greda.", 
                    9990.0, 
                    sopas, 
                    Arrays.asList("Choros", "Almejas", "Pescado", "Camarones", "Caldo de pescado")));

                // 3. Precarga de COMPLETOS
                repo.save(new Menu(null, 
                    "Completo Italiano", 
                    "Vienesa con tomate, palta y mayonesa casera.", 
                    4990.0, 
                    completos, 
                    Arrays.asList("Pan", "Vienesa", "Tomate", "Palta", "Mayonesa")));

                repo.save(new Menu(null, 
                    "Completo Dinámico", 
                    "El clásico con todo: tomate, palta, mayonesa, chucrut y salsa americana.", 
                    5290.0, 
                    completos, 
                    Arrays.asList("Pan", "Vienesa", "Tomate", "Palta", "Mayonesa", "Chucrut", "Salsa Americana")));

                // 4. Precarga de ASADOS
                repo.save(new Menu(null, 
                    "Asado todas las carnes", 
                    "Parrillada mixta para compartir con los mejores cortes de la casa.", 
                    34990.0, 
                    asados, 
                    Arrays.asList("Lomo", "Pollo", "Costillar", "Prieta", "Chorizo")));

                repo.save(new Menu(null, 
                    "Lomo a lo Pobre", 
                    "Corte de lomo liso acompañado de papas fritas, cebolla frita y dos huevos.", 
                    14990.0, 
                    asados, 
                    Arrays.asList("Lomo liso", "Papas fritas", "Cebolla", "Huevos")));
                
                System.out.println(">>> Precarga de Menú y Categorías finalizada con éxito.");
        
        };
    }
}
