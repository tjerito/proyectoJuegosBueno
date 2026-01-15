package com.example.proyectoJuegos.Repositories;

import com.example.proyectoJuegos.Entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario,Long> {

    // Buscar un usuario exacto por su nombre
    Optional<Usuario> findByNombre(String nombre);

    // Buscar por email (muy útil para el Login)
    Optional<Usuario> findByEmail(String email);

    // Comprobar si ya existe un email antes de registrar a alguien
    boolean existsByEmail(String email);

    // Buscar usuarios creados después de una fecha concreta
    List<Usuario> findByFechaCreacionAfter(LocalDateTime fecha);

}
