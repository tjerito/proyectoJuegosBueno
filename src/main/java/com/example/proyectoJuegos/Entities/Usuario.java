package com.example.proyectoJuegos.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data //Para escriura automática de código redundante
@Entity //Hace entender al JPA que esta clase es una tabla
@Table(name = "Usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String nombre;
    private String email;
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGame> lista = new ArrayList<>();
}
