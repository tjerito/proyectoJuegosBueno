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

    //Este método indica que urls son publicas
    //Son las que pueden acceder cualquier tipo de usuario tenga o no token firmado
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**")).permitAll()
        );


        //Establece la configuracion
        super.configure(http);

        //Esto le dice a vaadin que alguien no está logueado lo lleve a la pestaña de login
        setLoginView(http, LoginView.class);
    }

    //Este método encripta contraseñas con bcrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}