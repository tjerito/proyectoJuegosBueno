package com.example.proyectoJuegos;

import com.example.proyectoJuegos.Config.JwtUtils;
import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Enums.Role;
import com.example.proyectoJuegos.Services.AuthenticatedUser;
import com.example.proyectoJuegos.Services.GeneroService;
import com.example.proyectoJuegos.Services.JuegoService;
import com.example.proyectoJuegos.Services.ReviewService;
import com.example.proyectoJuegos.Services.UserGameService;
import com.example.proyectoJuegos.Services.UsuarioService;
import com.example.proyectoJuegos.support.TestJwtSupport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@SpringBootTest(properties = "vaadin.launch-browser=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JuegoService juegoService;

    @MockBean
    private GeneroService generoService;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserGameService userGameService;

    @MockBean
    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    void setUp() {
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(usuarioService.existeEmail(anyString())).thenReturn(false);
        when(usuarioService.guardar(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            usuario.setFechaCreacion(LocalDateTime.now());
            return usuario;
        });
    }

    @Test
    void testAccesoSinToken() throws Exception {
        mockMvc.perform(get("/api/juegos"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains("No autorizado"));
    }

    @Test
    void testTokenManipulado() throws Exception {
        String token = TestJwtSupport.tokenWithWrongSignature("usuario@juegos.com", jwtSecret);

        mockMvc.perform(get("/api/juegos")
                        .header("Authorization", TestJwtSupport.bearer(token)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testTokenExpirado() throws Exception {
        String token = TestJwtSupport.expiredToken("usuario@juegos.com", jwtSecret);

        mockMvc.perform(get("/api/juegos")
                        .header("Authorization", TestJwtSupport.bearer(token)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRutasPublicasAccesibles() throws Exception {
        String email = "publico@juegos.com";
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNombre("Público");
        usuario.setEmail(email);
        usuario.setPassword("encoded-password");
        usuario.setRoles(Set.of(Role.USER));
        usuario.setFechaCreacion(LocalDateTime.now());

        when(usuarioService.buscarPorEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Password123", "encoded-password")).thenReturn(true);
        when(authenticatedUser.loadUserByUsername(email)).thenReturn(TestJwtSupport.principal(email, "USER"));

        Map<String, Object> loginRequest = Map.of(
                "email", email,
                "password", "Password123"
        );

        JsonNode loginBody = objectMapper.readTree(mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString());
        assertThat(loginBody.get("token").asText()).isNotBlank();
        assertThat(jwtUtils.validateToken(loginBody.get("token").asText())).isTrue();

        JsonNode registroBody = objectMapper.readTree(mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nombre", "Registro Público",
                                "email", "registro@juegos.com",
                                "password", "Password123"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString());
        assertThat(registroBody.get("token").asText()).isNotBlank();
        assertThat(jwtUtils.validateToken(registroBody.get("token").asText())).isTrue();

        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").exists());

        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }
}

