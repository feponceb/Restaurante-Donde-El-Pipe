package com.dondeElPipe.gestorCocina.DataLoader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.dondeElPipe.gestorCocina.model.EstadoCocina;
import com.dondeElPipe.gestorCocina.repository.EstadoCocinaRepository;

@Component
public class DataLoader implements CommandLineRunner{

    @Autowired
    private EstadoCocinaRepository estadoRepo;

    @Override
    public void run(String... args) throws Exception {
        // Validación para cargar datos solo si la tabla está vacía
        if (estadoRepo.count() == 0) {
            estadoRepo.save(new EstadoCocina(null, "EN_ESPERA"));
            estadoRepo.save(new EstadoCocina(null, "PREPARANDO"));
            estadoRepo.save(new EstadoCocina(null, "LISTO"));
            System.out.println(">> Catálogo de Estados de Cocina inicializado con éxito mediante DataLoader.");
        }
    }

}
