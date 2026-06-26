package com.dondeElPipe.gestorUsuario.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class Seguridad {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt aplica un hash seguro y aleatorio (Salt) automáticamente
        return new BCryptPasswordEncoder();
    }

}
