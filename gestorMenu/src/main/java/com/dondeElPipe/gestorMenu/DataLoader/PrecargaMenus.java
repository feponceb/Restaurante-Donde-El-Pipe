package com.dondeElPipe.gestorMenu.DataLoader;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorMenu.model.CategoriaMenu;
import com.dondeElPipe.gestorMenu.model.Ingrediente;
import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.repository.CategoriaMenuRepository;
import com.dondeElPipe.gestorMenu.repository.IngredienteRepository;
import com.dondeElPipe.gestorMenu.repository.MenuRepository;

@Configuration
public class PrecargaMenus {
    
    @Bean
    CommandLineRunner init(MenuRepository repo, CategoriaMenuRepository catRepo, IngredienteRepository ingRepo){
        return args -> {
            if (catRepo.count() == 0 && repo.count() == 0) {
                // 1. Creamos y guardamos las categorías base (Devuelven el objeto completo con su ID generado)
                CategoriaMenu sopas = catRepo.save(new CategoriaMenu(null, "SOPAS")); 
                CategoriaMenu completos = catRepo.save(new CategoriaMenu(null, "COMPLETOS")); 
                CategoriaMenu asados = catRepo.save(new CategoriaMenu(null, "ASADOS"));
                
                // 2. Creamos y guardamos los ingredientes base individuales
                Ingrediente carne = ingRepo.save(new Ingrediente(null, "Carne de Vacuno"));
                Ingrediente papa = ingRepo.save(new Ingrediente(null, "Papas"));
                Ingrediente zapallo = ingRepo.save(new Ingrediente(null, "Zapallo"));
                Ingrediente vienesa = ingRepo.save(new Ingrediente(null, "Vienesa"));
                Ingrediente tomate = ingRepo.save(new Ingrediente(null, "Tomate"));
                Ingrediente palta = ingRepo.save(new Ingrediente(null, "Palta"));
                Ingrediente mayo = ingRepo.save(new Ingrediente(null, "Mayonesa"));
                Ingrediente pollo = ingRepo.save(new Ingrediente(null, "Pollo"));
                Ingrediente cerdo = ingRepo.save(new Ingrediente(null, "Costillar de Cerdo"));

                // 3. Insertamos los platos pasándole los OBJETOS completos (Categoría e Ingredientes)
                // Estructura del constructor: Menu(id, nombrePlato, precio, objetoCategoria, listaDeIngredientes)
                
                repo.save(new Menu(
                    null, 
                    "Cazuela", 
                    8990.0, 
                    sopas, // Pasamos el objeto CategoriaMenu completo
                    List.of(carne, papa, zapallo) // Pasamos la lista de objetos Ingrediente
                ));

                repo.save(new Menu(
                    null, 
                    "Completo Italiano", 
                    4990.0, 
                    completos, 
                    List.of(vienesa, tomate, palta, mayo)
                ));

                repo.save(new Menu(
                    null, 
                    "Asado todas las carnes", 
                    34990.0, 
                    asados, 
                    List.of(carne, pollo, cerdo)
                ));
                
                System.out.println(">> ¡Precarga de Menús, Categorías e Ingredientes ejecutada con éxito! <<");
            }
        };
    }
}
