package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Config.JwtUtils;
import com.example.proyectoJuegos.Entities.LoginRequest;
import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

//Esta clase expone endpoints para loguear y registrar usuarios y generar tokens
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Nota: Necesitarás añadir AuthenticationManager en SecurityConfig para que esto funcione
    public AuthController(UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    //Se encargar de registrar usuarios
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        //Se verifica si el email ya existe
        if (usuarioService.existeEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El email ya existe");
        }
        // Ciframos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        //Guardamos en la base de datos
        return ResponseEntity.ok(usuarioService.guardar(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        //Buscamos al usuario que intenta iniciar sesion por su correo
        return usuarioService.buscarPorEmail(loginRequest.getEmail())
                //Comparamos la contraseña de la peticion con la del usuario
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    //Generamos un nuevo token para el usuario (Expira en 24H)
                    String token = jwtUtils.generarToken(user.getEmail());
                    // Devolvemos un Map con el token
                    return ResponseEntity.ok((Object) Map.of("token", token));
                })
                // Devolvemos un ResponseEntity con el mensaje de error directamente
                .orElseGet(() -> ResponseEntity.status(401).body("Credenciales inválidas"));
    }
}
