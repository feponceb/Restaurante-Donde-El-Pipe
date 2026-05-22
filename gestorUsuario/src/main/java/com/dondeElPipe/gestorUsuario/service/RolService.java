package com.dondeElPipe.gestorUsuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorUsuario.model.Rol;
import com.dondeElPipe.gestorUsuario.repository.RolRepository;

@Service
public class RolService {

    @Autowired
    private RolRepository repo;

    public Rol crear(Rol rol) {
        String nombreLimpio = rol.getNombre().trim().toUpperCase().replaceAll("\\s+", " ");
        if (repo.existsByNombreIgnoreCase(nombreLimpio)) {
            return null;
        }
        rol.setNombre(nombreLimpio);
        return repo.save(rol);
    }

    public List<Rol> listarTodo() {
        return repo.findAll();
    }

    public Optional<Rol> buscarPorId(Integer id) {
        return repo.findById(id);
    }

    public void eliminar(Integer id) {
        repo.deleteById(id);
    }

}
