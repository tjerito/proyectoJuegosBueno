package com.example.proyectoJuegos;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Arrays;
import java.util.Optional;
import com.example.proyectoJuegos.Entities.UserGame;
import com.example.proyectoJuegos.Enums.Estado;
import com.example.proyectoJuegos.Repositories.UserGameRepositorio;
import com.example.proyectoJuegos.Services.UserGameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserGameServiceTest {

    @Mock
    private UserGameRepositorio userGameRepositorio;

    @InjectMocks
    private UserGameService userGameService;

    @Test
    void testSumarHoras_Exito() {
        // GIVEN: Un registro de biblioteca existente con 10 horas
        UserGame registroExistente = new UserGame();
        registroExistente.setHorasJugadas(10);

        when(userGameRepositorio.findByUserIdAndGameId(1L, 5L))
                .thenReturn(Optional.of(registroExistente));

        // Simulamos que al guardar devuelve el objeto con las horas sumadas
        when(userGameRepositorio.save(any(UserGame.class))).thenAnswer(i -> i.getArguments()[0]);

        // WHEN: Sumamos 5 horas más
        UserGame resultado = userGameService.añadirHoras(1L, 5L, 5);

        // THEN: Verificamos que ahora tiene 15 horas
        assertNotNull(resultado);
        assertEquals(15, resultado.getHorasJugadas());
        verify(userGameRepositorio).save(any(UserGame.class));
    }

    @Test
    void testCambiarEstado_Exito() {
        // GIVEN: Un juego que está "PENDIENTE"
        UserGame registro = new UserGame();
        registro.setEstado(Estado.PENDIENTE);

        when(userGameRepositorio.findByUserIdAndGameId(1L, 1L))
                .thenReturn(Optional.of(registro));
        when(userGameRepositorio.save(any(UserGame.class))).thenAnswer(i -> i.getArguments()[0]);

        // WHEN: Cambiamos a "COMPLETADO"
        UserGame resultado = userGameService.cambiarEstado(1L, 1L, Estado.COMPLETADO);

        // THEN: Verificamos el cambio
        assertEquals(Estado.COMPLETADO, resultado.getEstado());
        verify(userGameRepositorio).save(registro);
    }

}
