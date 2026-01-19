package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.UserGame;
import com.example.proyectoJuegos.Enums.Estado;
import com.example.proyectoJuegos.Exceptions.ResourceNotFoundException;
import com.example.proyectoJuegos.Services.UserGameService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
public class UserGameController {

    private final UserGameService service;

    public UserGameController(UserGameService service) {
        this.service = service;
    }

    // 1. VER BIBLIOTECA DE UN USUARIO
    @GetMapping("/usuario/{userId}")
    public List<UserGame> verBiblioteca(@PathVariable Long userId) {
        List<UserGame> biblioteca = service.obtenerBibliotecaPorUsuario(userId);
        if (biblioteca.isEmpty()) {
            throw new ResourceNotFoundException("No se encontró biblioteca para el usuario con ID: " + userId);
        }
        return biblioteca;
    }

    // 2. AGREGAR JUEGO O ACTUALIZAR PROGRESO
    @PostMapping
    public ResponseEntity<UserGame> guardar(@Valid @RequestBody UserGame userGame) {
        // @Valid asegura que horasJugadas no sea negativo y rating esté en rango
        return new ResponseEntity<>(service.guardarProgreso(userGame), HttpStatus.CREATED);
    }

    // 3. AÑADIR HORAS A UN JUEGO
    @PatchMapping("/usuario/{userId}/juego/{gameId}/horas")
    public ResponseEntity<UserGame> sumarHoras(
            @PathVariable Long userId,
            @PathVariable Long gameId,
            @RequestParam int cantidad) {

        UserGame actualizado = service.añadirHoras(userId, gameId, cantidad);
        if (actualizado == null) {
            throw new ResourceNotFoundException("No se pudo actualizar: El usuario o el juego no existen en la biblioteca.");
        }
        return ResponseEntity.ok(actualizado);
    }

    // 4. CAMBIAR ESTADO
    @PutMapping("/usuario/{userId}/juego/{gameId}/estado")
    public ResponseEntity<UserGame> actualizarEstado(
            @PathVariable Long userId,
            @PathVariable Long gameId,
            @RequestBody Estado nuevoEstado) {

        UserGame actualizado = service.cambiarEstado(userId, gameId, nuevoEstado);
        if (actualizado == null) {
            throw new ResourceNotFoundException("No se pudo cambiar el estado: Registro no encontrado.");
        }
        return ResponseEntity.ok(actualizado);
    }

    // 5. FILTRAR POR ESTADO
    @GetMapping("/filtro")
    public List<UserGame> filtrar(@RequestParam Estado estado) {
        return service.buscarPorEstado(estado);
    }

}
