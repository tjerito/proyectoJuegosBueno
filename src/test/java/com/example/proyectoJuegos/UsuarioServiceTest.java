package com.example.proyectoJuegos; // Asegúrate que el package coincida con tu carpeta

import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Repositories.UsuarioRepositorio;
import com.example.proyectoJuegos.Services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepositorio usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void testBuscarPorId_Exito() {
        // GIVEN
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setNombre("Fran");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioMock));

        // WHEN
        Optional<Usuario> resultado = usuarioService.buscarPorId(1L);

        // THEN
        assertTrue(resultado.isPresent());
        assertEquals("Fran", resultado.get().getNombre());
        verify(usuarioRepository, times(1)).findById(1L);
    }
}