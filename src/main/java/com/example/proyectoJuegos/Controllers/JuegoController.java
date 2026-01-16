package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Services.JuegoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/juegos")
public class JuegoController {

    private final JuegoService service;

    public JuegoController(JuegoService service) {
        this.service = service;
    }

    // 1. LISTAR TODOS: GET http://localhost:8081/api/juegos
    @GetMapping
    public List<Juego> listar() {
        return service.listarTodos();
    }

    // 2. BUSCAR POR ID: GET http://localhost:8081/api/juegos/1
    @GetMapping("/{id}")
    public ResponseEntity<Juego> porId(@PathVariable Long id) {
        return service.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. BUSCAR POR NOMBRE (PARCIAL): GET http://localhost:8081/api/juegos/buscar?nombre=Witch
    @GetMapping("/buscar")
    public List<Juego> buscarPorNombre(@RequestParam String nombre) {
        return service.buscarPorNombreParcial(nombre);
    }

    // 4. TOP 5 NOVEDADES: GET http://localhost:8081/api/juegos/novedades
    @GetMapping("/novedades")
    public List<Juego> obtenerNovedades() {
        return service.obtenerTop5Novedades();
    }

    // 5. LANZAMIENTOS DESDE FECHA: GET http://localhost:8081/api/juegos/recientes?fecha=2023-01-01
    @GetMapping("/recientes")
    public List<Juego> lanzamientosRecientes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.buscarLanzamientosRecientes(fecha);
    }

    // 6. GUARDAR: POST http://localhost:8081/api/juegos
    @PostMapping
    public Juego crear(@RequestBody Juego juego) {
        return service.guardar(juego);
    }

}
