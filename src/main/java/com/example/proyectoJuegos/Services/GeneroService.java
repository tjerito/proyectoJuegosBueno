package com.example.proyectoJuegos.Services;

import com.example.proyectoJuegos.Entities.Genero;
import com.example.proyectoJuegos.Repositories.GeneroRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeneroService {

    private final GeneroRepositorio repo;

    // Inyección por constructor: La mejor práctica para la capa de servicios
    public GeneroService(GeneroRepositorio repo) {
        this.repo = repo;
    }

    // --- MÉTODOS CRUD ---

    public List<Genero> listarTodos() {
        return repo.findAll();
    }

    public Optional<Genero> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Genero guardar(Genero genero) {
        // Podríamos añadir lógica para que el nombre siempre se guarde en mayúsculas
        if (genero.getNombre() != null) {
            genero.setNombre(genero.getNombre().toUpperCase());
        }
        return repo.save(genero);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // --- BÚSQUEDA PERSONALIZADA ---

    public Optional<Genero> buscarPorNombre(String nombre) {
        // Gracias al IgnoreCase del repositorio, no importa si buscan "rpg" o "RPG"
        return repo.findByNombreIgnoreCase(nombre);
    }

}
