package com.example.proyectoJuegos.Services;

import com.example.proyectoJuegos.Entities.Review;
import com.example.proyectoJuegos.Repositories.ReviewRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepositorio repo;

    // Inyección por constructor
    public ReviewService(ReviewRepositorio repo) {
        this.repo = repo;
    }

    // --- MÉTODOS DE PERSISTENCIA ---

    public Review guardar(Review review) {
        // Lógica de negocio: Si es una review nueva, le ponemos la fecha de hoy automáticamente
        if (review.getId() == 0) {
            review.setFechaReview(LocalDate.now());
        }
        return repo.save(review);
    }

    public List<Review> listarTodas() {
        return repo.findAll();
    }

    public Optional<Review> buscarPorId(Long id) {
        return repo.findById(id);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    // --- MÉTODOS DE BÚSQUEDA PERSONALIZADOS ---

    public List<Review> buscarPorJuego(Long juegoId) {
        // Útil para mostrar todas las críticas en la ficha de un juego
        return repo.findByJuegoId(juegoId);
    }

    public List<Review> buscarPorAutor(Long usuarioId) {
        // Útil para mostrar el historial de críticas en el perfil de un usuario
        return repo.findByAutorId(usuarioId);
    }

    public List<Review> buscarMejoresReviews(Integer notaMinima) {
        // Útil para un widget de "Críticas destacadas"
        return repo.findByRatingGreaterThanEqual(notaMinima);
    }

}
