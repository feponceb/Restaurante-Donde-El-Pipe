package com.dondeElPipe.gestorUsuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorUsuario.DTO.UsuarioDTO;
import com.dondeElPipe.gestorUsuario.model.Usuario;
import com.dondeElPipe.gestorUsuario.repository.RolRepository;
import com.dondeElPipe.gestorUsuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    //inyeción del repository
    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private RolRepository rolRepo;

    //--+----+----+----+----+----+----+----+----+----+----+
    //--+----+----+----+--Crud básico--+----+----+----+----+----
    //--+----+----+----+----+----+----+----+----+----+----+

    //ver todos los usuarios
    public List<Usuario> listar(){
        return repo.findAll();
    } 

    //crear un usuario
    public Usuario crearUsuario(Usuario usuario) {

        String rutLimpio = usuario.getRut().trim().replace(".", "").replace("-", "");
    
        // 1. Validar RUT Matemático
        if (!validarRutMatematico(rutLimpio)) {
            throw new IllegalArgumentException("El RUT ingresado no es válido matemáticamente.");
        }

        // 2. Validar que el Rol Exista
        if (usuario.getRol() == null || !rolRepo.existsById(usuario.getRol())) {
            throw new IllegalArgumentException("El ID de Rol asignado no existe en el sistema.");
        }

        // 3. CONDICIONALES DE NEGOCIO: Validamos duplicados manualmente antes de guardar
        if (repo.existsByRutIgnoreCase(usuario.getRut().trim())) {
            throw new IllegalArgumentException("El RUT ya está registrado");
        }

        if (repo.existsByEmailIgnoreCase(usuario.getEmail().trim())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Si pasa todos los filtros, persistimos el objeto sanitizado
        usuario.setRut(usuario.getRut().trim());
        usuario.setEmail(usuario.getEmail().trim().toLowerCase());

        return repo.save(usuario);
    }


    //eliminar un usuario por el id
    public void eliminarUsuario(Integer id){
        repo.deleteById(id);
    } 

    //modificar un usuario
    public Usuario actualizarUsuario(Integer id, Usuario usuario){
        // 1. Limpiamos y validamos el RUT también al modificar
        String rutLimpio = usuario.getRut().trim().replace(".", "").replace("-", "");
        
        if (!validarRutMatematico(rutLimpio)) {
            return null; // Frena la actualización si inventaron un RUT
        }

        // 2. Si el rol que intentan asignar no existe, no dejamos guardar
        if (usuario.getRol() == null || !rolRepo.existsById(usuario.getRol())) {
            return null;
        }
        usuario.setId(id);
        return repo.save(usuario);
    }

    //--+----+----+----+----+----+----+----+----+----+----+
    //--+----+----+--Funciones especiales--+----+----+----+----
    //--+----+----+----+----+----+----+----+----+----+----+

    //buscar por id
    public Optional<Usuario> buscarId(Integer id){
        return repo.findById(id);
    }

    // Algoritmo matemático para validar el RUT chileno (Módulo 11)
    private boolean validarRutMatematico(String rut) {
        // Un RUT limpio válido debe tener entre 8 y 9 caracteres (ej: 12345678K)
        if (rut == null || rut.length() < 8) {
            return false;
        }
        
        try {
            // Extraer el dígito verificador (el último caracter)
            char dvIngresado = rut.charAt(rut.length() - 1);
            
            // Extraer la parte numérica del RUT
            String cuerpoRut = rut.substring(0, rut.length() - 1);
            int rutNumerico = Integer.parseInt(cuerpoRut);

            // Algoritmo Módulo 11
            int suma = 0;
            int multiplicador = 2;

            while (rutNumerico > 0) {
                int resto = rutNumerico % 10;
                suma += resto * multiplicador;
                rutNumerico = rutNumerico / 10;
                multiplicador = (multiplicador == 7) ? 2 : multiplicador + 1;
            }

            int restoSuma = 11 - (suma % 11);
            char dvEsperado;

            if (restoSuma == 11) {
                dvEsperado = '0';
            } else if (restoSuma == 10) {
                dvEsperado = 'K';
            } else {
                dvEsperado = (char) (restoSuma + '0');
            }

            // Retorna verdadero si el dígito calculado coincide con el ingresado por el usuario
            return dvIngresado == dvEsperado;

        } catch (NumberFormatException e) {
            return false; // Retorna falso si el cuerpo contiene caracteres inválidos que no sean números
        }
    }

    // Listar todo mapeado a DTO
    public List<UsuarioDTO> listarDTO() {
        List<Usuario> usuarios = repo.findAll();

        return usuarios.stream().map(u -> {
            String nombreRol = rolRepo.findById(u.getRol())
                                      .map(r -> r.getNombre())
                                      .orElse("SIN ROL");

            return new UsuarioDTO(
                u.getRut(),
                u.getNombre(),
                u.getApellido(),
                u.getEmail(),
                nombreRol
            );
        }).toList();
    }

}
