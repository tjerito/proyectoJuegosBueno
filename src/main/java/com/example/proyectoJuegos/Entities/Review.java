package com.example.proyectoJuegos.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "Reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "El comentario no puede estar vacío")
    @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres")
    private String comentario;

    @NotNull(message = "La puntuación es obligatoria")
    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 5, message = "La puntuación máxima es 5")
    private Integer rating;

    @NotNull(message = "La fecha de la reseña es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate fechaReview;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(hidden = true)
    @JsonIgnore
    @ManyToOne
    @NotNull(message = "La reseña debe tener un autor")
    @JoinColumn(name = "usuario_id")
    private Usuario autor;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Schema(hidden = true)
    @JsonIgnore
    @ManyToOne
    @NotNull(message = "La reseña debe estar asociada a un juego")
    @JoinColumn(name = "juego_id")
    private Juego juego;

    @PrePersist
    protected void onCreate() {
        this.fechaReview = LocalDate.now();
    }
}
