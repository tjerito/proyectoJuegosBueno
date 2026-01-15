package com.example.proyectoJuegos.Repositories;

import com.example.proyectoJuegos.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepositorio extends JpaRepository<Review,Long> {

    // Ver todas las reviews de un juego concreto
    List<Review> findByJuegoId(Long juegoId);

    // Ver todas las reviews de un autor (Usuario)
    List<Review> findByAutorId(Long usuarioId);

    // Buscar reviews con una puntuación alta (ej: 9 o 10)
    List<Review> findByRatingGreaterThanEqual(Integer rating);

}
