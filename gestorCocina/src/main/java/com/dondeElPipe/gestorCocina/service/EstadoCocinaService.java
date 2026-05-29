package com.dondeElPipe.gestorCocina.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorCocina.model.EstadoCocina;
import com.dondeElPipe.gestorCocina.repository.EstadoCocinaRepository;

@Service
public class EstadoCocinaService {

    @Autowired
    private EstadoCocinaRepository estadoRepo;

    // Buscar un estado por su nombre (Ej: para asignarlo por defecto)
    public EstadoCocina buscarPorNombre(String nombre) {
        return estadoRepo.findByNombreIgnoreCase(nombre)
            .orElseThrow(() -> new IllegalArgumentException("El estado '" + nombre + "' no existe en la base de datos."));
    }

    // Buscar por ID (Ej: para las validaciones de los controladores)
    public EstadoCocina buscarPorId(Integer id) {
        return estadoRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("El ID de estado " + id + " no es válido."));
    }

}
