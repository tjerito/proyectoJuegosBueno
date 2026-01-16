package com.example.proyectoJuegos.Services;

import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Repositories.JuegoRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JuegoService {

    private final JuegoRepositorio repo;

    // Inyección por constructor: limpia, segura y fácil de testear
    public JuegoService(JuegoRepositorio repo) {
        this.repo = repo;
    }

    // --- OPERACIONES CRUD ---

    public List<Juego> listarTodos() {
        return repo.findAll();
    }

    public Optional<Juego> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public Juego guardar(Juego juego) {
        // Aquí podrías añadir lógica, como por ejemplo:
        // Asegurar que el título siempre se guarde con la primera letra en mayúscula
        return repo.save(juego);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // --- BÚSQUEDAS PERSONALIZADAS ---

    public Optional<Juego> buscarPorTituloExacto(String titulo) {
        return repo.findByTitulo(titulo);
    }

    public List<Juego> buscarPorNombreParcial(String palabra) {
        // Ideal para buscadores en tiempo real en la web
        return repo.findByTituloContainingIgnoreCase(palabra);
    }

    public List<Juego> buscarLanzamientosRecientes(LocalDate desde) {
        return repo.findByFechaSalidaAfter(desde);
    }

    public List<Juego> obtenerTop5Novedades() {
        // Este método es genial para un carrusel de "Novedades" en la página de inicio
        return repo.findTop5ByOrderByFechaSalidaDesc();
    }
}
