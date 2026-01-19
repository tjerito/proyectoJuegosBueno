package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.Juego;
import com.example.proyectoJuegos.Exceptions.ResourceNotFoundException;
import com.example.proyectoJuegos.Services.JuegoService;
import jakarta.validation.Valid;
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

    // 1. LISTAR TODOS
    @GetMapping
    public List<Juego> listar() {
        return service.listarTodos();
    }

    // 2. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Juego> porId(@PathVariable Long id) {
        // Cambiamos el .orElse(notFound) por nuestra excepción personalizada
        Juego juego = service.obtenerPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el juego con ID: " + id));
        return ResponseEntity.ok(juego);
    }

    // 3. BUSCAR POR NOMBRE (PARCIAL)
    @GetMapping("/buscar")
    public List<Juego> buscarPorNombre(@RequestParam String nombre) {
        List<Juego> resultados = service.buscarPorNombreParcial(nombre);
        if (resultados.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron juegos que coincidan con: " + nombre);
        }
        return resultados;
    }

    // 4. TOP 5 NOVEDADES
    @GetMapping("/novedades")
    public List<Juego> obtenerNovedades() {
        return service.obtenerTop5Novedades();
    }

    // 5. LANZAMIENTOS DESDE FECHA
    @GetMapping("/recientes")
    public List<Juego> lanzamientosRecientes(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return service.buscarLanzamientosRecientes(fecha);
    }

    // 6. GUARDAR
    @PostMapping
    public ResponseEntity<Juego> crear(@Valid @RequestBody Juego juego) {
        // Añadimos @Valid para que se ejecuten las reglas (@NotBlank, @Size, etc.)
        // Cambiamos el retorno a ResponseEntity para ser más profesionales
        return ResponseEntity.status(201).body(service.guardar(juego));
    }

}
