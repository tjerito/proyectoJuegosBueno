package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Config.JwtUtils;
import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Enums.Role;
import com.example.proyectoJuegos.Services.UsuarioService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "vaadin.launch-browser=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegistro_exitoso() throws Exception {
        when(usuarioService.existeEmail("nuevo@juegos.com")).thenReturn(false);
        when(passwordEncoder.encode("Password123")).thenReturn("encoded-password");
        when(usuarioService.guardar(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            usuario.setFechaCreacion(LocalDateTime.now());
            return usuario;
        });

        Map<String, Object> request = Map.of(
                "nombre", "Nuevo Jugador",
                "email", "nuevo@juegos.com",
                "password", "Password123"
        );

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.email").value("nuevo@juegos.com"))
                .andExpect(jsonPath("$.usuario.nombre").value("Nuevo Jugador"));
    }

    @Test
    void testRegistro_emailDuplicado() throws Exception {
        when(usuarioService.existeEmail("duplicado@juegos.com")).thenReturn(true);

        Map<String, Object> request = Map.of(
                "nombre", "Jugador Duplicado",
                "email", "duplicado@juegos.com",
                "password", "Password123"
        );

        mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains("email ya existe"));
    }

    @Test
    void testLogin_exitoso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setNombre("Login User");
        usuario.setEmail("login@juegos.com");
        usuario.setPassword("encoded-password");
        usuario.setRoles(Set.of(Role.USER));
        usuario.setFechaCreacion(LocalDateTime.now());

        when(usuarioService.buscarPorEmail("login@juegos.com")).thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(true);

        Map<String, Object> loginRequest = Map.of(
                "email", "login@juegos.com",
                "password", "Password123"
        );

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = body.get("token").asText();
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    void testLogin_credencialesInvalidas() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setEmail("login-fallo@juegos.com");
        usuario.setPassword("encoded-password");

        when(usuarioService.buscarPorEmail("login-fallo@juegos.com")).thenReturn(java.util.Optional.of(usuario));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(false);

        Map<String, Object> loginRequest = Map.of(
                "email", "login-fallo@juegos.com",
                "password", "Password123"
        );

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains("Credenciales inválidas"));
    }
}

