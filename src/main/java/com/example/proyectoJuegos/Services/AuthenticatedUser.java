package com.example.proyectoJuegos.Services;

import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Repositories.UsuarioRepositorio;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUser implements UserDetailsService {
    private final UsuarioRepositorio usuarioRepositorio;

    public AuthenticatedUser(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Intentando login con: " + username); // <--- AÑADE ESTO

        Usuario usuario = usuarioRepositorio.findByNombre(username)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado en BD");
                    return new UsernameNotFoundException("No existe");
                });

        System.out.println("Usuario encontrado, comprobando password...");
        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getNombre())
                .password(usuario.getPassword())
                .roles("USER")
                .build();
    }
}
