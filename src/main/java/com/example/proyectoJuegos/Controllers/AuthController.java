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

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        if (usuarioService.existeEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El email ya existe");
        }
        // Ciframos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return ResponseEntity.ok(usuarioService.guardar(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return usuarioService.buscarPorEmail(loginRequest.getEmail())
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    String token = jwtUtils.generarToken(user.getEmail());
                    // Devolvemos un Map con el token
                    return ResponseEntity.ok((Object) Map.of("token", token));
                })
                // Devolvemos un ResponseEntity con el mensaje de error directamente
                .orElseGet(() -> ResponseEntity.status(401).body("Credenciales inválidas"));
    }
}
