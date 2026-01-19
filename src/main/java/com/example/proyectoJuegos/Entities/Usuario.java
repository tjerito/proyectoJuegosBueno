package com.example.proyectoJuegos.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe proporcionar un formato de email válido")
    @Column(unique = true) // Garantiza unicidad a nivel de base de datos
    private String email;

    @NotNull(message = "La fecha de creación no puede ser nula")
    @PastOrPresent(message = "La fecha de creación no puede ser futura")
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGame> lista = new ArrayList<>();

    // Método de ayuda para asignar la fecha automáticamente antes de persistir
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
