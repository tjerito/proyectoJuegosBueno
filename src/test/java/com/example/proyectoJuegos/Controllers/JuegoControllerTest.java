package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Config.JwtUtils;
import com.example.proyectoJuegos.Entities.Genero;
import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Services.AuthenticatedUser;
import com.example.proyectoJuegos.Services.JuegoService;
import com.example.proyectoJuegos.support.TestJwtSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "vaadin.launch-browser=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class JuegoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private JuegoService juegoService;

    @MockBean
    private AuthenticatedUser authenticatedUser;

    @Test
    void testGetJuegos_sinAutenticar() throws Exception {
        mockMvc.perform(get("/api/juegos"))
                .andExpect(status().isUnauthorized())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains("No autorizado"));
    }

    @Test
    void testGetJuegos_conToken() throws Exception {
        String email = "usuario@juegos.com";
        String token = jwtUtils.generarToken(email);
        UserDetails principal = TestJwtSupport.principal(email, "USER");
        when(authenticatedUser.loadUserByUsername(email)).thenReturn(principal);

        Juego juego = new Juego();
        juego.setId(1L);
        juego.setTitulo("Elden Ring");
        juego.setDescripcion("RPG de mundo abierto");
        juego.setFechaSalida(LocalDate.of(2022, 2, 25));
        juego.setUrlImagen("https://example.com/elden-ring.jpg");
        juego.setGeneros(List.of(new Genero() {{ setNombre("RPG"); }}));
        when(juegoService.listarTodos()).thenReturn(List.of(juego));

        mockMvc.perform(get("/api/juegos")
                        .header("Authorization", TestJwtSupport.bearer(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Elden Ring"));
    }

    @Test
    void testCrearJuego_sinAdmin() throws Exception {
        String email = "user@juegos.com";
        String token = jwtUtils.generarToken(email);
        when(authenticatedUser.loadUserByUsername(email)).thenReturn(TestJwtSupport.principal(email, "USER"));

        mockMvc.perform(post("/api/juegos")
                        .header("Authorization", TestJwtSupport.bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "titulo", "Juego Nuevo",
                                "descripcion", "Descripción válida del juego",
                                "fechaSalida", "2024-01-01",
                                "urlImagen", "https://example.com/juego-nuevo.jpg",
                                "generos", List.of(Map.of("nombre", "Acción"))
                        ))))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCrearJuego_conAdmin() throws Exception {
        String email = "admin@juegos.com";
        String token = jwtUtils.generarToken(email);
        when(authenticatedUser.loadUserByUsername(email)).thenReturn(TestJwtSupport.principal(email, "ADMIN"));
        when(juegoService.guardar(any(Juego.class))).thenAnswer(invocation -> {
            Juego juego = invocation.getArgument(0);
            juego.setId(99L);
            return juego;
        });

        mockMvc.perform(post("/api/juegos")
                        .header("Authorization", TestJwtSupport.bearer(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "titulo", "Juego Admin",
                                "descripcion", "Descripción válida del juego",
                                "fechaSalida", "2024-01-01",
                                "urlImagen", "https://example.com/juego-admin.jpg",
                                "generos", List.of(Map.of("nombre", "Acción"))
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99L))
                .andExpect(jsonPath("$.titulo").value("Juego Admin"));
    }
}

