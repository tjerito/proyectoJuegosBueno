package com.example.proyectoJuegos.Services;

import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Repositories.UsuarioRepositorio;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class AuthenticatedUser implements UserDetailsService {
    private final UsuarioRepositorio usuarioRepositorio;

    public AuthenticatedUser(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("--- INTENTO DE LOGIN ---");
        System.out.println("Buscando a: " + username);

        Usuario usuario = usuarioRepositorio.findByNombre(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        System.out.println("Usuario encontrado en BD: " + usuario.getNombre());
        System.out.println("Password en BD (debe empezar por $2a$): " + usuario.getPassword());

        return usuario;
    }
}
