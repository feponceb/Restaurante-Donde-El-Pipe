package com.dondeElPipe.gestorUsuario.DataLoad;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.dondeElPipe.gestorUsuario.model.Rol;
import com.dondeElPipe.gestorUsuario.model.Usuario;
import com.dondeElPipe.gestorUsuario.repository.RolRepository;
import com.dondeElPipe.gestorUsuario.repository.UsuarioRepository;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner init(UsuarioRepository repo, RolRepository rolRepo, PasswordEncoder passwordEncoder) { // 💡 Inyectamos el encoder aquí
        return args -> {
            // Validación doble para asegurar que las tablas estén limpias antes de poblar
            if (rolRepo.count() == 0 && repo.count() == 0) {
                
                // 1. Guardar Roles de forma directa
                rolRepo.save(new Rol(null, "ADMIN"));      // Tomará el ID 1
                rolRepo.save(new Rol(null, "GARZON"));     // Tomará el ID 2
                rolRepo.save(new Rol(null, "COCINA"));     // Tomará el ID 3

                // 2. Guardar Único Administrador (ID Rol: 1) con clave encriptada
                repo.save(new Usuario(null, "20909720-6", "Donde El", "Pipe", 1, "administrador@dondeelpipe.com", passwordEncoder.encode("admin1234")));

                // 3. Guardar Varios Garzones (ID Rol: 2) con claves encriptadas
                repo.save(new Usuario(null, "19876543-2", "Andres", "Perez", 2, "andres@dondeelpipe.com", passwordEncoder.encode("garzon123")));
                repo.save(new Usuario(null, "15642879-K", "Maria", "Soto", 2, "maria@dondeelpipe.com", passwordEncoder.encode("garzon456")));
                repo.save(new Usuario(null, "18234915-7", "Carlos", "Munoz", 2, "carlos@dondeelpipe.com", passwordEncoder.encode("garzon789")));

                // 4. Guardar Personal de Cocina (ID Rol: 3) con claves encriptadas
                repo.save(new Usuario(null, "10277265-2", "Juan", "Gomez", 3, "juan@dondeelpipe.com", passwordEncoder.encode("cocina123")));
                repo.save(new Usuario(null, "14108532-6", "Elena", "Rios", 3, "elena@dondeelpipe.com", passwordEncoder.encode("cocina456")));
                
                System.out.println(">>> Personal de 'Donde El Pipe' precargado con éxito encriptado con BCrypt.");
                
            }
        };
    }

}
