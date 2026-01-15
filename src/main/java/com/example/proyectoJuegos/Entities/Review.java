package com.example.proyectoJuegos.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "Reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String comentario;
    //Usamos Integer para el manejo de tipos nulos en la base de datos
    private Integer rating;
    private LocalDate fechaReview;

    @ManyToOne
    //Este es basicamente el nombre que quieras que tenga la columna en tu tabla
    //Quieres sabe la id del autor el cual ha escrito la reseña en este caso
    @JoinColumn(name = "usuario_id")
    private Usuario autor;

    @ManyToOne
    //Lo mismo pasa con el juego al que va dirigido la reseña
    @JoinColumn(name = "juego_id")
    private Juego juego;
}
