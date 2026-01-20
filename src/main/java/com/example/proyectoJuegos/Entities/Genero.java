package com.example.proyectoJuegos.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "Generos")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del género es obligatorio")
    @Size(min = 3, max = 30, message = "El nombre del género debe tener entre 3 y 30 caracteres")
    @Column(unique = true) // Evita que existan dos géneros llamados igual (ej. "Acción" y "Acción")
    private String nombre;

}
