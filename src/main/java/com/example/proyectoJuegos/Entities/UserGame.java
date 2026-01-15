package com.example.proyectoJuegos.Entities;

import com.example.proyectoJuegos.Enums.Estado;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "user_game")
public class UserGame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Estado estado;
    private Integer horasJugadas;
    private Integer rating;
    private LocalDate fechaAdicion;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Usuario user;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Juego game;
}
