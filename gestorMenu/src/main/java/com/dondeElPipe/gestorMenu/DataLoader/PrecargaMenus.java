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
                // 1. Guardamos las categorías base de los platos
                CategoriaMenu sopas = catRepo.save(new CategoriaMenu(null, "SOPAS"));         // ID 1
                CategoriaMenu completos = catRepo.save(new CategoriaMenu(null, "COMPLETOS")); // ID 2
                CategoriaMenu asados = catRepo.save(new CategoriaMenu(null, "ASADOS"));       // ID 3

                // 2. Insertamos los platos pasándole el ID numérico directo (.getId())
                repo.save(new Menu(null, "Cazuela", 8990.0, sopas.getId()));
                repo.save(new Menu(null, "Completo Italiano", 4990.0, completos.getId()));
                repo.save(new Menu(null, "Asado todas las carnes", 34990.0, asados.getId()));
            }
        };
    }
}
