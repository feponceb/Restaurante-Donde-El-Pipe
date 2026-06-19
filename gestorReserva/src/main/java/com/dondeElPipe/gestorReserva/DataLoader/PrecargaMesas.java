package com.dondeElPipe.gestorReserva.DataLoader;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dondeElPipe.gestorReserva.model.EstadoMesa;
import com.dondeElPipe.gestorReserva.model.Mesa;
import com.dondeElPipe.gestorReserva.repository.EstadoMesaRepository;
import com.dondeElPipe.gestorReserva.repository.MesaRepository;

@Configuration
public class PrecargaMesas {

    @Bean
    CommandLineRunner initMesas(MesaRepository mesaRepo, EstadoMesaRepository estadoMesaRepo) {
        return args -> {
            // Validación doble para asegurar que las tablas estén limpias antes de poblar
            if (estadoMesaRepo.count() == 0 && mesaRepo.count() == 0) {
                
                // 1. Guardar Estados de forma directa
                estadoMesaRepo.save(new EstadoMesa(null, "Habilitada")); // Tomará el ID 1
                estadoMesaRepo.save(new EstadoMesa(null, "Ocupada"));    // Tomará el ID 2
                estadoMesaRepo.save(new EstadoMesa(null, "Reservada"));  // Tomará el ID 3

                // 2. Guardar Mesas utilizando la referencia del ID del Estado (fiel al estilo rol)
                // Como el ID es IDENTITY, se guardarán correlativamente del 1 al 6 (Número de mesa = ID)
                mesaRepo.save(new Mesa(null, 2, 1)); // Mesa 1 (Parejas)
                mesaRepo.save(new Mesa(null, 2, 1)); // Mesa 2 (Parejas)
                mesaRepo.save(new Mesa(null, 4, 1)); // Mesa 3 (Familiar chica)
                mesaRepo.save(new Mesa(null, 4, 1)); // Mesa 4 (Familiar chica)
                mesaRepo.save(new Mesa(null, 6, 1)); // Mesa 5 (Familiar grande)
                mesaRepo.save(new Mesa(null, 8, 1)); // Mesa 6 (Cumpleaños / Banquetes)
                
            }
        };
    }
}
