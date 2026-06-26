package com.dondeElPipe.gestorUsuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repo,
        PasswordEncoder passwordEncoder
    ) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    //--+----+----+----+----+----+----+----+----+----+----+
    //--+----+----+----+--Crud básico--+----+----+----+----+----
    //--+----+----+----+----+----+----+----+----+----+----+

    //ver todos los usuarios
    public List<UsuarioDTO> listar() {
        List<Usuario> usuarios = repo.findAll();
        return usuarios.stream()
                       .map(this::convertirADto)
                       .toList();
    } 

    //crear un usuario
    public UsuarioDTO crearUsuario(Usuario usuario) {
        // 1. Validaciones iniciales de campos obligatorios (incluyendo que traiga password)
        if (usuario.getRut() == null || usuario.getEmail() == null || 
            usuario.getRol() == null || usuario.getPassword() == null) {
            return null;
        }
        
        String rutLimpio = usuario.getRut().trim().replace(".", "").replace("-", "");
        String emailLimpio = usuario.getEmail().trim().toLowerCase();
        
        // Validaciones de negocio y duplicados
        if (!validarRutMatematico(rutLimpio) || 
            !rolRepo.existsById(usuario.getRol()) || 
            repo.existsByRutIgnoreCase(usuario.getRut().trim()) || 
            repo.existsByEmailIgnoreCase(emailLimpio)) {
            return null; 
        }

        // 2. Persistimos los datos sanitizados
        usuario.setRut(usuario.getRut().trim());
        usuario.setEmail(emailLimpio);

        // 3.  ENCRIPTACIÓN DE LA CLAVE (Según la documentación adjunta)
        // Extraemos el password en texto plano enviado desde Postman, lo codificamos con BCrypt
        // y lo reasignamos al objeto antes de hacer el save.
        String passwordPlano = usuario.getPassword();
        String passwordEncriptado = passwordEncoder.encode(passwordPlano);
        usuario.setPassword(passwordEncriptado);

        // 4. Guardamos en la base de datos de manera segura
        Usuario usuarioGuardado = repo.save(usuario);
        
        // Retornamos el DTO mapeado (el cual ya filtra el password automáticamente)
        return convertirADto(usuarioGuardado);
    }


    //eliminar un usuario por el id
    public void eliminarUsuario(Integer id){
        repo.deleteById(id);
    } 

    //modificar un usuario
    public Usuario actualizarUsuario(Integer id, Usuario usuario) {
        if (usuario.getRut() == null || usuario.getEmail() == null || usuario.getRol() == null) {
            return null;
        }

        String rutLimpio = usuario.getRut().trim().replace(".", "").replace("-", "").toUpperCase();
        String emailLimpio = usuario.getEmail().trim().toLowerCase();

        if (!validarRutMatematico(rutLimpio) || !rolRepo.existsById(usuario.getRol())) {
            return null;
        }

        Optional<Usuario> usuarioPorRut = repo.findByRutIgnoreCase(rutLimpio);
        if (usuarioPorRut.isPresent() && !usuarioPorRut.get().getId().equals(id)) {
            return null; 
        }

        Optional<Usuario> usuarioPorEmail = repo.findByEmailIgnoreCase(emailLimpio);
        if (usuarioPorEmail.isPresent() && !usuarioPorEmail.get().getId().equals(id)) {
            return null; 
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
    public Optional<UsuarioDTO> buscarId(Integer id) {
        return repo.findById(id).map(this::convertirADto);
    }

    //transformadorDTO
    private UsuarioDTO convertirADto(Usuario u) {
        String nombreRol = rolRepo.findById(u.getRol())
                                  .map(r -> r.getNombre())
                                  .orElse("SIN ROL");

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setRut(u.getRut());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setNombreRol(nombreRol);
        return dto;
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
