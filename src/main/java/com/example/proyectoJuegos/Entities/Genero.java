package com.example.proyectoJuegos.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Generos")
public class Genero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;

}
