package com.example.proyectoJuegos.Repositories;

import com.example.proyectoJuegos.Entities.UserGame;
import com.example.proyectoJuegos.Enums.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//Se usa para que SpringBoot como interfaz para poder inyectarlas en otras partes del codigo

@Repository
//Al extender de la interfaz JpaRepository, tenemos metodos por defecto para usar
//Pudiendo crear metodos personalizados
public interface UserGameRepositorio extends JpaRepository<UserGame, Long> {

    // Obtener todos los juegos de un usuario específico
    List<UserGame> findByUserId(Long userId);

    // Buscar registros por un estado concreto (ej: "COMPLETADO")
    // Suponiendo que tu Enum se llama Estado
    List<UserGame> findByEstado(Estado estado);

    // Buscar juegos donde el usuario haya jugado más de X horas
    List<UserGame> findByHorasJugadasGreaterThan(Integer horas);

    // Buscar si un usuario ya tiene un juego concreto en su lista
    Optional<UserGame> findByUserIdAndGameId(Long userId, Long gameId);
}
