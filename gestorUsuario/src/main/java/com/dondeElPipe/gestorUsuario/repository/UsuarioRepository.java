package com.dondeElPipe.gestorUsuario.repository;
import org.springframework.stereotype.Repository;
import com.dondeElPipe.gestorUsuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    boolean existsByRutIgnoreCase(String rut);

    boolean existsByEmailIgnoreCase(String email);

}