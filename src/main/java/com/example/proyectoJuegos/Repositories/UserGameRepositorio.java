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


    List<UserGame> findByUserId(Long userId);


    List<UserGame> findByEstado(Estado estado);


    List<UserGame> findByHorasJugadasGreaterThan(Integer horas);


    Optional<UserGame> findByUserIdAndGameId(Long userId, Long gameId);
}
