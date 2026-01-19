package com.example.proyectoJuegos.Entities;

import com.example.proyectoJuegos.Enums.Estado;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "user_game")
public class UserGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "El estado del juego (Jugando, Completado, etc.) es obligatorio")
    @Enumerated(EnumType.STRING) // Recomendado para que en la BD se guarde el texto del Enum
    private Estado estado;

    @Min(value = 0, message = "Las horas jugadas no pueden ser negativas")
    private Integer horasJugadas;

    @Min(value = 1, message = "La puntuación mínima es 1")
    @Max(value = 10, message = "La puntuación máxima es 10")
    private Integer rating;

    @NotNull(message = "La fecha de adición es obligatoria")
    @PastOrPresent(message = "La fecha de adición no puede ser futura")
    private LocalDate fechaAdicion;

    @ManyToOne
    @NotNull(message = "Debe estar asociado a un usuario")
    @JoinColumn(name = "user_id")
    private Usuario user;

    @ManyToOne
    @NotNull(message = "Debe estar asociado a un juego")
    @JoinColumn(name = "game_id")
    private Juego game;

    @PrePersist
    protected void onCreate() {
        if (this.fechaAdicion == null) {
            this.fechaAdicion = LocalDate.now();
        }
        if (this.horasJugadas == null) {
            this.horasJugadas = 0;
        }
    }
}
