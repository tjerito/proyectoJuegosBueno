package com.example.proyectoJuegos.Services;

import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Repositories.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    // Atributo final para asegurar que se inicialice por constructor
    private final UsuarioRepositorio repo;

    // Inyección por constructor (No hace falta @Autowired en versiones modernas)
    public UsuarioService(UsuarioRepositorio repo) {
        this.repo = repo;
    }

    // --- MÉTODOS CRUD BÁSICOS (Vienen por defecto en la interfaz) ---

    public List<Usuario> listarTodos() {
        return repo.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public Usuario guardar(Usuario usuario) {
        // Lógica de negocio: Si es un usuario nuevo, le asignamos la fecha de creación actual
        if (usuario.getId() == 0) { // O usuario.getId() == null si cambiaste a Long
            usuario.setFechaCreacion(LocalDateTime.now());
        }
        return repo.save(usuario);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // --- MÉTODOS DERIVADOS DE LA INTERFAZ ---

    public Optional<Usuario> buscarPorNombre(String nombre) {
        return repo.findByNombre(nombre);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return repo.findByEmail(email);
    }

    public boolean existeEmail(String email) {
        return repo.existsByEmail(email);
    }

    public List<Usuario> buscarUsuariosNuevos(LocalDateTime desde) {
        return repo.findByFechaCreacionAfter(desde);
    }

}
