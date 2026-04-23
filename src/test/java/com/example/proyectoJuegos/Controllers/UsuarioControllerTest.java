package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Config.JwtUtils;
import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Enums.Role;
import com.example.proyectoJuegos.Services.AuthenticatedUser;
import com.example.proyectoJuegos.Services.UsuarioService;
import com.example.proyectoJuegos.support.TestJwtSupport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "vaadin.launch-browser=false")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private AuthenticatedUser authenticatedUser;

    @Test
    void testDeleteUsuario_sinAdmin() throws Exception {
        String email = "user@juegos.com";
        String token = jwtUtils.generarToken(email);
        when(authenticatedUser.loadUserByUsername(email)).thenReturn(TestJwtSupport.principal(email, "USER"));

        mockMvc.perform(delete("/api/usuarios/1")
                        .header("Authorization", TestJwtSupport.bearer(token)))
                .andExpect(status().isForbidden())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString()).contains("Acceso denegado"));
    }

    @Test
    void testDeleteUsuario_conAdmin() throws Exception {
        String email = "admin@juegos.com";
        String token = jwtUtils.generarToken(email);
        when(authenticatedUser.loadUserByUsername(email)).thenReturn(TestJwtSupport.principal(email, "ADMIN"));

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("A eliminar");
        usuario.setEmail("eliminar@juegos.com");
        usuario.setPassword("encoded-password");
        usuario.setRoles(Set.of(Role.USER));
        usuario.setFechaCreacion(LocalDateTime.now());

        when(usuarioService.buscarPorId(1L)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioService).eliminar(1L);

        mockMvc.perform(delete("/api/usuarios/1")
                        .header("Authorization", TestJwtSupport.bearer(token)))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetUsuarios_conToken() throws Exception {
        String email = "usuario@juegos.com";
        String token = jwtUtils.generarToken(email);
        when(authenticatedUser.loadUserByUsername(email)).thenReturn(TestJwtSupport.principal(email, "USER"));

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Lista Uno");
        usuario.setEmail("lista1@juegos.com");
        usuario.setPassword("encoded-password");
        usuario.setRoles(Set.of(Role.USER));
        usuario.setFechaCreacion(LocalDateTime.now());
        when(usuarioService.listarTodos()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/usuarios")
                        .header("Authorization", TestJwtSupport.bearer(token))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("lista1@juegos.com"));
    }
}

