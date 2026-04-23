package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Config.JwtUtils;
import com.example.proyectoJuegos.Entities.LoginRequest;
import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Enums.Role;
import com.example.proyectoJuegos.Services.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Set;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

//Esta clase expone endpoints para loguear y registrar usuarios y generar tokens
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long BLOCK_WINDOW_MILLIS = TimeUnit.MINUTES.toMillis(15);
    private static final Map<String, Integer> FAILED_ATTEMPTS_BY_IP = new ConcurrentHashMap<>();
    private static final Map<String, Long> FIRST_FAILURE_BY_IP = new ConcurrentHashMap<>();

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final String adminSecret;

    // Nota: Necesitarás añadir AuthenticationManager en SecurityConfig para que esto funcione
    public AuthController(UsuarioService usuarioService, PasswordEncoder passwordEncoder, JwtUtils jwtUtils,
                          @Value("${admin.secret}") String adminSecret) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.adminSecret = adminSecret;
    }

    //Se encargar de registrar usuarios
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestHeader(value = "X-Admin-Secret", required = false) String headerAdminSecret,
                                      @RequestBody Usuario usuario) {
        //Se verifica si el email ya existe
        if (usuarioService.existeEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El email ya existe");
        }

        boolean quiereSerAdmin = adminSecret.equals(headerAdminSecret);
        usuario.setRoles(quiereSerAdmin ? Set.of(Role.ADMIN) : Set.of(Role.USER));
        // Ciframos la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        //Guardamos en la base de datos y devolvemos también un JWT para usar la cuenta recién creada
        Usuario guardado = usuarioService.guardar(usuario);
        String token = jwtUtils.generarToken(guardado.getEmail());
        return ResponseEntity.ok(Map.of("token", token, "usuario", guardado));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        String clientIp = getClientIp(request);

        if (isBlocked(clientIp)) {
            return ResponseEntity.status(429).body("Demasiados intentos fallidos. Intenta más tarde.");
        }

        //Buscamos al usuario que intenta iniciar sesion por su correo
        return usuarioService.buscarPorEmail(loginRequest.getEmail())
                //Comparamos la contraseña de la peticion con la del usuario
                .filter(user -> passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> {
                    clearFailures(clientIp);
                    //Generamos un nuevo token para el usuario (Expira en 24H)
                    String token = jwtUtils.generarToken(user.getEmail());
                    // Devolvemos un Map con el token
                    return ResponseEntity.ok((Object) Map.of("token", token));
                })
                // Devolvemos un ResponseEntity con el mensaje de error directamente
                .orElseGet(() -> {
                    registerFailure(clientIp);
                    if (isBlocked(clientIp)) {
                        return ResponseEntity.status(429).body("Demasiados intentos fallidos. Intenta más tarde.");
                    }
                    return ResponseEntity.status(401).body("Credenciales inválidas");
                });
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private boolean isBlocked(String ip) {
        Long firstFailure = FIRST_FAILURE_BY_IP.get(ip);
        Integer attempts = FAILED_ATTEMPTS_BY_IP.get(ip);
        if (firstFailure == null || attempts == null) {
            return false;
        }

        long elapsed = System.currentTimeMillis() - firstFailure;
        if (elapsed > BLOCK_WINDOW_MILLIS) {
            clearFailures(ip);
            return false;
        }

        return attempts >= MAX_FAILED_ATTEMPTS;
    }

    private void registerFailure(String ip) {
        long now = System.currentTimeMillis();
        FIRST_FAILURE_BY_IP.compute(ip, (key, firstFailure) -> {
            if (firstFailure == null || now - firstFailure > BLOCK_WINDOW_MILLIS) {
                FAILED_ATTEMPTS_BY_IP.put(key, 1);
                return now;
            }
            FAILED_ATTEMPTS_BY_IP.merge(key, 1, Integer::sum);
            return firstFailure;
        });
    }

    private void clearFailures(String ip) {
        FAILED_ATTEMPTS_BY_IP.remove(ip);
        FIRST_FAILURE_BY_IP.remove(ip);
    }
}
