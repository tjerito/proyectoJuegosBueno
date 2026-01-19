package com.example.proyectoJuegos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Optional;

import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Entities.Review;
import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Repositories.ReviewRepositorio;
import com.example.proyectoJuegos.Services.ReviewService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepositorio reviewRepositorio;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    void testGuardarReview_Exito() {
        // GIVEN: Preparamos los datos de prueba
        Usuario autor = new Usuario();
        autor.setId(1L);

        Juego juego = new Juego();
        juego.setId(1L);

        Review reviewMock = new Review();
        reviewMock.setComentario("Increíble juego");
        reviewMock.setRating(5);
        reviewMock.setAutor(autor);
        reviewMock.setJuego(juego);

        // Simulamos que el repositorio guarda y devuelve la review con un ID
        Review reviewGuardada = reviewMock;
        reviewGuardada.setId(100L);
        when(reviewRepositorio.save(any(Review.class))).thenReturn(reviewGuardada);

        // WHEN: Ejecutamos el guardado
        Review resultado = reviewService.guardar(reviewMock);

        // THEN: Verificaciones
        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals(5, resultado.getRating());
        verify(reviewRepositorio, times(1)).save(reviewMock);
    }

    @Test
    void testBuscarPorId_NoEncontrado() {
        // GIVEN: El repositorio devuelve vacío
        when(reviewRepositorio.findById(99L)).thenReturn(Optional.empty());

        // WHEN
        Optional<Review> resultado = reviewService.buscarPorId(99L);

        // THEN
        assertFalse(resultado.isPresent());
        verify(reviewRepositorio).findById(99L);
    }

}
