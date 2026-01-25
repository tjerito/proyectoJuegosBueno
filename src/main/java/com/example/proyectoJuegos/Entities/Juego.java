package com.example.proyectoJuegos.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "Juegos")
public class Juego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El título del juego es obligatorio")
    @Size(min = 2, max = 100, message = "El título debe tener entre 2 y 100 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @NotNull(message = "La fecha de salida es obligatoria")
    @PastOrPresent(message = "La fecha de salida no puede ser una fecha futura")
    private LocalDate fechaSalida;

    // --- NUEVO ATRIBUTO PARA LA IMAGEN ---
    @URL(message = "Debe ser una URL válida (ej: http://... o https://...)")
    private String urlImagen;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGame> lista = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "juego_genero",
            joinColumns = @JoinColumn(name = "juego_id"),
            inverseJoinColumns = @JoinColumn(name = "genero_id")
    )
    // Validamos que al menos tenga un género asignado si es necesario

    @NotEmpty(message = "El juego debe pertenecer al menos a un género")
    private List<Genero> generos = new ArrayList<>();

    // --- AÑADE ESTO ---
    @OneToMany(mappedBy = "juego", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

}
