package com.example.proyectoJuegos.Repositories;

import com.example.proyectoJuegos.Entities.Genero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneroRepositorio extends JpaRepository<Genero, Long> {

    // Buscar el género por su nombre
    Optional<Genero> findByNombreIgnoreCase(String nombre);

}
