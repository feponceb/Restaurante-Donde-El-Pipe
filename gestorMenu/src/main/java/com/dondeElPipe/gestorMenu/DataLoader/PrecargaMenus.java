package com.dondeElPipe.gestorMenu.DataLoader;


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
            if (catRepo.count() == 0 && repo.count() == 0) {
                catRepo.save(new CategoriaMenu(null, "SOPAS"));      
                catRepo.save(new CategoriaMenu(null, "COMPLETOS"));   
                catRepo.save(new CategoriaMenu(null, "ASADOS"));

                repo.save(new Menu(null, "Cazuela", 8990.0, new CategoriaMenu(1, null)));
                repo.save(new Menu(null, "Mariscal Caliente", 9990.0, new CategoriaMenu(1, null)));

                repo.save(new Menu(null, "Completo Italiano", 4990.0, new CategoriaMenu(2, null)));
                repo.save(new Menu(null, "Completo Dinámico", 5290.0, new CategoriaMenu(2, null)));

                repo.save(new Menu(null, "Asado todas las carnes", 34990.0, new CategoriaMenu(3, null)));
                repo.save(new Menu(null, "Lomo a lo Pobre", 14990.0, new CategoriaMenu(3, null)));      
                
            }
        };
    }
}
