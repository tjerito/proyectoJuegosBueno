package com.example.proyectoJuegos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Repositories.JuegoRepositorio;
import com.example.proyectoJuegos.Services.JuegoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class JuegoServiceTest {

    @Mock
    private JuegoRepositorio juegoRepositorio; // Simulamos el repo

    @InjectMocks
    private JuegoService juegoService; // Inyectamos el mock en el servicio

    @Test
    void testObtenerPorId_Encontrado() {
        // GIVEN: Preparamos un juego de prueba
        Juego juegoMock = new Juego();
        juegoMock.setId(10L);
        juegoMock.setTitulo("Elden Ring");

        // Definimos el comportamiento del mock
        when(juegoRepositorio.findById(10L)).thenReturn(Optional.of(juegoMock));

        // WHEN: Ejecutamos el método del servicio
        Optional<Juego> resultado = juegoService.obtenerPorId(10L);

        // THEN: Verificamos los resultados
        assertTrue(resultado.isPresent());
        assertEquals("Elden Ring", resultado.get().getTitulo());
        verify(juegoRepositorio, times(1)).findById(10L);
    }

    @Test
    void testObtenerTop5Novedades() {
        // GIVEN: Creamos una lista simulada de 2 juegos (aunque el método sea Top 5)
        Juego j1 = new Juego();
        j1.setTitulo("Juego Reciente 1");
        Juego j2 = new Juego();
        j2.setTitulo("Juego Reciente 2");

        List<Juego> listaSimulada = Arrays.asList(j1, j2);

        when(juegoRepositorio.findTop5ByOrderByFechaSalidaDesc()).thenReturn(listaSimulada);

        // WHEN
        List<Juego> resultado = juegoService.obtenerTop5Novedades();

        // THEN
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juego Reciente 1", resultado.get(0).getTitulo());
        // Verificamos que se llamó al método correcto del repositorio
        verify(juegoRepositorio).findTop5ByOrderByFechaSalidaDesc();
    }

}
