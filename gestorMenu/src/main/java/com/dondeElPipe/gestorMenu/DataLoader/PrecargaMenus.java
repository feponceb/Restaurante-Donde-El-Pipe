package com.dondeElPipe.gestorMenu.DataLoader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorMenu.model.Menu;
import com.dondeElPipe.gestorMenu.repository.MenuRepository;

@Configuration
public class PrecargaMenus {
    
    @Bean
    CommandLineRunner init(MenuRepository repo){
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Menu(null, "Cazuela", 8990.0, "Sopas"));
                repo.save(new Menu(null, "Completo Italiano", 4990.0, "Completo"));
                repo.save(new Menu(null, "Asado todas las carnes", 34990.0, "Asado"));
            }
        };
    }
}
