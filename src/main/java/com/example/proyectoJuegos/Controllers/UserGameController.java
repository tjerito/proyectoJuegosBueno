package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.UserGame;
import com.example.proyectoJuegos.Enums.Estado;
import com.example.proyectoJuegos.Services.UserGameService;
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

    // 1. VER BIBLIOTECA DE UN USUARIO: GET http://localhost:8081/api/biblioteca/usuario/1
    @GetMapping("/usuario/{userId}")
    public List<UserGame> verBiblioteca(@PathVariable Long userId) {
        return service.obtenerBibliotecaPorUsuario(userId);
    }

    // 2. AGREGAR JUEGO O ACTUALIZAR PROGRESO: POST http://localhost:8081/api/biblioteca
    @PostMapping
    public ResponseEntity<UserGame> guardar(@RequestBody UserGame userGame) {
        return new ResponseEntity<>(service.guardarProgreso(userGame), HttpStatus.CREATED);
    }

    // 3. AÑADIR HORAS A UN JUEGO: PATCH http://localhost:8081/api/biblioteca/usuario/1/juego/5/horas?cantidad=10
    @PatchMapping("/usuario/{userId}/juego/{gameId}/horas")
    public ResponseEntity<UserGame> sumarHoras(
            @PathVariable Long userId,
            @PathVariable Long gameId,
            @RequestParam int cantidad) {

        UserGame actualizado = service.añadirHoras(userId, gameId, cantidad);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    // 4. CAMBIAR ESTADO (EJ: A COMPLETADO): PUT http://localhost:8081/api/biblioteca/usuario/1/juego/5/estado
    @PutMapping("/usuario/{userId}/juego/{gameId}/estado")
    public ResponseEntity<UserGame> actualizarEstado(
            @PathVariable Long userId,
            @PathVariable Long gameId,
            @RequestBody Estado nuevoEstado) {

        UserGame actualizado = service.cambiarEstado(userId, gameId, nuevoEstado);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    // 5. FILTRAR POR ESTADO: GET http://localhost:8081/api/biblioteca/filtro?estado=COMPLETADO
    @GetMapping("/filtro")
    public List<UserGame> filtrar(@RequestParam Estado estado) {
        return service.buscarPorEstado(estado);
    }

}
