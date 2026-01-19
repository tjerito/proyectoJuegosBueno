package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.Review;
import com.example.proyectoJuegos.Exceptions.ResourceNotFoundException;
import com.example.proyectoJuegos.Services.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    // 1. OBTENER TODAS
    @GetMapping
    public List<Review> listar() {
        return service.listarTodas();
    }

    // 2. BUSCAR POR JUEGO
    @GetMapping("/juego/{juegoId}")
    public List<Review> porJuego(@PathVariable Long juegoId) {
        List<Review> reviews = service.buscarPorJuego(juegoId);
        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("No hay reseñas para el juego con ID: " + juegoId);
        }
        return reviews;
    }

    // 3. BUSCAR POR AUTOR
    @GetMapping("/autor/{usuarioId}")
    public List<Review> porAutor(@PathVariable Long usuarioId) {
        List<Review> reviews = service.buscarPorAutor(usuarioId);
        if (reviews.isEmpty()) {
            throw new ResourceNotFoundException("El usuario con ID " + usuarioId + " no ha escrito reseñas.");
        }
        return reviews;
    }

    // 4. FILTRAR POR NOTA
    @GetMapping("/destacadas")
    public List<Review> mejoresReviews(@RequestParam(name = "min", defaultValue = "5") Integer notaMinima) {
        return service.buscarMejoresReviews(notaMinima);
    }

    // 5. PUBLICAR REVIEW
    @PostMapping
    public ResponseEntity<Review> crear(@Valid @RequestBody Review review) {
        // @Valid asegura que el rating esté entre 1-5 y el comentario no sea nulo
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(review));
    }

    // 6. ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(r -> {
                    service.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Reseña no encontrada con ID: " + id));
    }
}
