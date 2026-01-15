package com.example.proyectoJuegos.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Juegos")
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaSalida;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGame> lista = new ArrayList<>();

    //Con esto s crearia la tabla intermedia en la relacion entre Juegos y generos
    @ManyToMany
    @JoinTable(
            name = "juego_genero",
            joinColumns = @JoinColumn(name = "juego_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    private List<Genero> generos = new ArrayList<>();
}
