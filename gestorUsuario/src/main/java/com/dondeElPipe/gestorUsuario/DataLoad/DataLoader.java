package com.dondeElPipe.gestorUsuario.DataLoad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorUsuario.model.Rol;
import com.dondeElPipe.gestorUsuario.model.Usuario;
import com.dondeElPipe.gestorUsuario.repository.RolRepository;
import com.dondeElPipe.gestorUsuario.repository.UsuarioRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner init(UsuarioRepository repo, RolRepository rolRepo) {
        return args -> {
            // Validación doble para asegurar que las tablas estén limpias antes de poblar
            if (rolRepo.count() == 0 && repo.count() == 0) {
                
                // 1. Guardar Roles de forma directa
                rolRepo.save(new Rol(null, "ADMIN"));      // Tomará el ID 1
                rolRepo.save(new Rol(null, "GARZON"));     // Tomará el ID 2
                rolRepo.save(new Rol(null, "COCINA"));     // Tomará el ID 3

                // 2. Guardar Usuarios utilizando la referencia del ID del Rol (fiel al estilo CategoriaMenu)
                // Usamos RUTs chilenos matemáticamente válidos para que no fallen tus reglas del Service
                repo.save(new Usuario(null, "20909720-6", "Donde El", "Pipe", 1, "administrador@dondeelpipe.com", "admin1234"));
                repo.save(new Usuario(null, "19876543-2", "Andres", "Perez", 2, "andres@dondeelpipe.com", "garzon123"));
                repo.save(new Usuario(null, "10277265-2", "Juan", "Gomez", 3, "juan@dondeelpipe.com", "cocina123"));
                
            }
        };
    }

}
