package com.dondeElPipe.gestorUsuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dondeElPipe.gestorUsuario.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer>{
    boolean existsByNombreIgnoreCase(String nombre);
}
