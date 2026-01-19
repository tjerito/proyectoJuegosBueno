package com.example.proyectoJuegos.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Configuration
@EnableWebSecurity
@Component
public class SecurityConfig {

    // 1. Declaramos el filtro para que el IDE lo reconozca
    private final JwtAuthenticationFilter jwtAuthFilter;

    // 2. Lo inyectamos por constructor
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // PERMISOS CRÍTICOS PARA VAADIN (Añade estos exactamente)
                        .requestMatchers("/", "/index.html", "/vaadinServlet/**", "/frontend/**",
                                "/VAADIN/**", "/webjars/**", "/favicon.ico", "/sw.js").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().permitAll() // Cambia temporalmente a permitAll para probar que carga
                )
                // 3. Ya no saldrá en rojo porque jwtAuthFilter existe ahora como variable
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
