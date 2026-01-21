package com.example.proyectoJuegos.Services;

import com.example.proyectoJuegos.Entities.Genero;
import com.example.proyectoJuegos.Repositories.GeneroRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GeneroService {

    private final GeneroRepositorio repo;

    public GeneroService(GeneroRepositorio repo) {
        this.repo = repo;
    }


    public List<Genero> listarTodos() {
        return repo.findAll();
    }

    public Optional<Genero> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Genero guardar(Genero genero) {
        if (genero.getNombre() != null) {
            genero.setNombre(genero.getNombre().toUpperCase());
        }
        return repo.save(genero);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }


    public Optional<Genero> buscarPorNombre(String nombre) {
        return repo.findByNombreIgnoreCase(nombre);
    }

}
