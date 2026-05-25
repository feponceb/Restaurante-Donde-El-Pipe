package com.dondeElPipe.gestorPedidos.DataLoader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorPedidos.model.EstadoMesa;
import com.dondeElPipe.gestorPedidos.model.Mesa;
import com.dondeElPipe.gestorPedidos.repository.MesaRepository;

@Configuration
public class PrecargaMesas {

    @Bean
    CommandLineRunner initMesas(MesaRepository mesaRepo) {
        return args -> {
            // Si la tabla de mesas está completamente vacía, creamos el comedor base
            if (mesaRepo.count() == 0) {
                // Como el ID es IDENTITY, se guardarán correlativamente del 1 al 6 (Número de mesa = ID)
                mesaRepo.save(new Mesa(null, 2, EstadoMesa.Habilitada)); // Mesa 1 (Parejas)
                mesaRepo.save(new Mesa(null, 2, EstadoMesa.Habilitada)); // Mesa 2 (Parejas)
                mesaRepo.save(new Mesa(null, 4, EstadoMesa.Habilitada)); // Mesa 3 (Familiar chica)
                mesaRepo.save(new Mesa(null, 4, EstadoMesa.Habilitada)); // Mesa 4 (Familiar chica)
                mesaRepo.save(new Mesa(null, 6, EstadoMesa.Habilitada)); // Mesa 5 (Familiar grande)
                mesaRepo.save(new Mesa(null, 8, EstadoMesa.Habilitada)); // Mesa 6 (Cumpleaños / Banquetes)
                
                System.out.println(">> Comedor de 'Donde El Pipe' inicializado con éxito (Mesas de la 1 a la 6).");
            }
        };
    }

}
