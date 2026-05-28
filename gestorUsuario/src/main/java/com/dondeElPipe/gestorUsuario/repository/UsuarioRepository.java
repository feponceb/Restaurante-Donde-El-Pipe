package com.dondeElPipe.gestorUsuario.repository;
import org.springframework.stereotype.Repository;
import com.dondeElPipe.gestorUsuario.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByRutIgnoreCase(String rut);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByRut(String rut);

    Optional<Usuario> findByRutIgnoreCase(String trim);

    Optional<Usuario> findByEmailIgnoreCase(String emailLimpio);

}