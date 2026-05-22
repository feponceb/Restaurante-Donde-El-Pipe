package com.dondeElPipe.gestorUsuario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dondeElPipe.gestorUsuario.model.Usuario;
import com.dondeElPipe.gestorUsuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    //inyeción del repository
    @Autowired
    private UsuarioRepository repo;

    //--+----+----+----+----+----+----+----+----+----+----+
    //--+----+----+----+--Crud básico--+----+----+----+----+----
    //--+----+----+----+----+----+----+----+----+----+----+

    //ver todos los usuarios
    public List<Usuario> listar(){
        return repo.findAll();
    } 

    //crear un usuario
    public Usuario crearUsuario(Usuario usuario) {
        
        String rutLimpio = usuario.getRut().replace(".", "").replace("-", "").replaceAll("\\s+", "").toUpperCase();

        if (!validarRutMatematico(rutLimpio)) {
            return null; 
        }

        if (repo.existsByRutIgnoreCase(rutLimpio)) {
            return null; 
        }

        usuario.setRut(rutLimpio);
        
        return repo.save(usuario);
    }


    //eliminar un usuario por el id
    public void eliminarUsuario(Integer id){
        repo.deleteById(id);
    } 

    //modificar un usuario
    public Usuario actualizarUsuario(Integer id, Usuario usuario){
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

}
