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

    public JuegoService(JuegoRepositorio repo) {
        this.repo = repo;
    }


    public List<Juego> listarTodos() {
        return repo.findAll();
    }

    public Optional<Juego> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public Juego guardar(Juego juego) {
        return repo.save(juego);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }


    public Optional<Juego> buscarPorTituloExacto(String titulo) {
        return repo.findByTitulo(titulo);
    }

    public List<Juego> buscarPorNombreParcial(String palabra) {
        return repo.findByTituloContainingIgnoreCase(palabra);
    }

    public List<Juego> buscarLanzamientosRecientes(LocalDate desde) {
        return repo.findByFechaSalidaAfter(desde);
    }

    public List<Juego> obtenerTop5Novedades() {
        return repo.findTop5ByOrderByFechaSalidaDesc();
    }
}
