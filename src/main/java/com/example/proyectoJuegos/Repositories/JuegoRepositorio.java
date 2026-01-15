package com.example.proyectoJuegos.Repositories;

import com.example.proyectoJuegos.Entities.Juego;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JuegoRepositorio extends JpaRepository<Juego, Long> {

    // Buscar por título exacto
    Optional<Juego> findByTitulo(String titulo);

    // Buscar juegos que contengan una palabra (ej: "Witcher") ignorando mayúsculas
    List<Juego> findByTituloContainingIgnoreCase(String palabra);

    // Buscar juegos lanzados después de un año específico
    List<Juego> findByFechaSalidaAfter(LocalDate fecha);

    // Buscar los 5 juegos más recientes
    List<Juego> findTop5ByOrderByFechaSalidaDesc();

}
