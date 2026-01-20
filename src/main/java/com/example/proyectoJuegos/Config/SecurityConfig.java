package com.example.proyectoJuegos.Config;

import com.example.proyectoJuegos.Views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 1. Configuramos los permisos de las rutas primero
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**")).permitAll()
        );

        // 2. IMPORTANTE: Llamamos al configure de Vaadin al FINAL
        // Esto evita el error "Can't configure requestMatchers after anyRequest"
        super.configure(http);

        // 3. Registramos la vista de login
        setLoginView(http, LoginView.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}