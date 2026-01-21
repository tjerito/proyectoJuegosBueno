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

    public ReviewService(ReviewRepositorio repo) {
        this.repo = repo;
    }


    public Review guardar(Review review) {
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


    public List<Review> buscarPorJuego(Long juegoId) {
        return repo.findByJuegoId(juegoId);
    }

    public List<Review> buscarPorAutor(Long usuarioId) {
        return repo.findByAutorId(usuarioId);
    }

    public List<Review> buscarMejoresReviews(Integer notaMinima) {
        return repo.findByRatingGreaterThanEqual(notaMinima);
    }

}
