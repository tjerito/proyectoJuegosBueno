package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Repositories.JuegoRepositorio;
import com.example.proyectoJuegos.Repositories.ReviewRepositorio;
import com.example.proyectoJuegos.Repositories.UsuarioRepositorio;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UsuarioRepositorio usuarioRepositorio;
    private final JuegoRepositorio juegoRepositorio;
    private final ReviewRepositorio reviewRepositorio;

    public AdminController(UsuarioRepositorio usuarioRepositorio, JuegoRepositorio juegoRepositorio, ReviewRepositorio reviewRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.juegoRepositorio = juegoRepositorio;
        this.reviewRepositorio = reviewRepositorio;
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> estadisticas() {
        long totalUsuarios = usuarioRepositorio.count();
        long totalJuegos = juegoRepositorio.count();
        long totalReviews = reviewRepositorio.count();

        String juegoMasRevieweado = reviewRepositorio.findAll().stream()
                .collect(Collectors.groupingBy(review -> review.getJuego().getTitulo(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin reseñas");

        return Map.of(
                "totalUsuarios", totalUsuarios,
                "totalJuegos", totalJuegos,
                "totalReviews", totalReviews,
                "juegoMasRevieweado", juegoMasRevieweado
        );
    }
}

