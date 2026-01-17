package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.Review;
import com.example.proyectoJuegos.Services.ReviewService;
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

    // 1. OBTENER TODAS: GET http://localhost:8081/api/reviews
    @GetMapping
    public List<Review> listar() {
        return service.listarTodas();
    }

    // 2. BUSCAR POR JUEGO: GET http://localhost:8081/api/reviews/juego/1
    @GetMapping("/juego/{juegoId}")
    public List<Review> porJuego(@PathVariable Long juegoId) {
        return service.buscarPorJuego(juegoId);
    }

    // 3. BUSCAR POR AUTOR: GET http://localhost:8081/api/reviews/autor/2
    @GetMapping("/autor/{usuarioId}")
    //@PathVariable sirve para capturar valores directamente de la URL (La id en este caso)
    public List<Review> porAutor(@PathVariable Long usuarioId) {
        return service.buscarPorAutor(usuarioId);
    }

    // 4. FILTRAR POR NOTA: GET http://localhost:8081/api/reviews/destacadas?min=8
    @GetMapping("/destacadas")
    //@RequestParam se usa para extraer datos que vienen al final de la URL despues de un "?"
    public List<Review> mejoresReviews(@RequestParam(name = "min", defaultValue = "5") Integer notaMinima) {
        return service.buscarMejoresReviews(notaMinima);
    }

    // 5. PUBLICAR REVIEW: POST http://localhost:8081/api/reviews
    @PostMapping
    public ResponseEntity<Review> crear(@RequestBody Review review) {
        // El service asignará la fecha automáticamente si el ID es 0
        return ResponseEntity.ok(service.guardar(review));
    }

    // 6. ELIMINAR: DELETE http://localhost:8081/api/reviews/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.buscarPorId(id).isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
