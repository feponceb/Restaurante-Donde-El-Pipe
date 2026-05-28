package com.dondeElPipe.gestorUsuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorUsuario.DTO.UsuarioDTO;
import com.dondeElPipe.gestorUsuario.model.Usuario;
import com.dondeElPipe.gestorUsuario.repository.RolRepository;
import com.dondeElPipe.gestorUsuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    //inyeción del repository
    private final UsuarioRepository repo;
    private final RolRepository rolRepo;

    public UsuarioService(UsuarioRepository repo, RolRepository rolRepo) {
        this.repo = repo;
        this.rolRepo = rolRepo;
    }


    //--+----+----+----+----+----+----+----+----+----+----+
    //--+----+----+----+--Crud básico--+----+----+----+----+----
    //--+----+----+----+----+----+----+----+----+----+----+

    //ver todos los usuarios
    public List<Usuario> listar(){
        return repo.findAll();
    } 

    //crear un usuario
    public Usuario crearUsuario(Usuario usuario) {
        if (usuario.getRut() == null || usuario.getEmail() == null || usuario.getRol() == null) {
            return null;
        }
        String rutLimpio = usuario.getRut().trim().replace(".", "").replace("-", "");
        String emailLimpio = usuario.getEmail().trim().toLowerCase();
        if (!validarRutMatematico(rutLimpio) || 
            !rolRepo.existsById(usuario.getRol()) || 
            repo.existsByRutIgnoreCase(usuario.getRut().trim()) || 
            repo.existsByEmailIgnoreCase(emailLimpio)) {
            return null; 
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
        if (usuario.getRut() == null || usuario.getEmail() == null || usuario.getRol() == null) {
            return null;
        }

        // 1. Limpiamos el RUT igual que en la creación
        String rutLimpio = usuario.getRut().trim().replace(".", "").replace("-", "").toUpperCase();
        String emailLimpio = usuario.getEmail().trim().toLowerCase();

        if (!validarRutMatematico(rutLimpio) || !rolRepo.existsById(usuario.getRol())) {
            return null;
        }

        // 2. Verificamos duplicados usando el RUT limpio
        Optional<Usuario> usuarioPorRut = repo.findByRutIgnoreCase(rutLimpio);
        if (usuarioPorRut.isPresent() && !usuarioPorRut.get().getId().equals(id)) {
            return null; // El RUT ya lo tiene otra persona
        }

        Optional<Usuario> usuarioPorEmail = repo.findByEmailIgnoreCase(emailLimpio);
        if (usuarioPorEmail.isPresent() && !usuarioPorEmail.get().getId().equals(id)) {
            return null; // El Email ya lo tiene otra persona
        }

        usuario.setId(id);
        usuario.setRut(usuario.getRut().trim());
        usuario.setEmail(emailLimpio);
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
            return Character.toUpperCase(dvIngresado) == dvEsperado;

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

            UsuarioDTO dto = new UsuarioDTO();
            dto.setRut(u.getRut());
            dto.setNombre(u.getNombre());
            dto.setApellido(u.getApellido());
            dto.setEmail(u.getEmail());
            dto.setNombreRol(nombreRol);
            return dto;
        }).toList();
    }

}
