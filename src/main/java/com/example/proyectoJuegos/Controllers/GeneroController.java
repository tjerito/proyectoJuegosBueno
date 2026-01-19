package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.Genero;
import com.example.proyectoJuegos.Exceptions.ResourceNotFoundException;
import com.example.proyectoJuegos.Services.GeneroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/generos")
public class GeneroController {

    private final GeneroService service;

    public GeneroController(GeneroService service) {
        this.service = service;
    }

    // 1. LISTAR TODOS
    @GetMapping
    public List<Genero> obtenerTodos() {
        return service.listarTodos();
    }

    // 2. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Genero> obtenerPorId(@PathVariable Long id) {
        // Usamos orElseThrow para que tu GlobalExceptionHandler capture el error 404
        Genero genero = service.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con ID: " + id));
        return ResponseEntity.ok(genero);
    }

    // 3. BUSCAR POR NOMBRE
    @GetMapping("/buscar")
    public ResponseEntity<Genero> obtenerPorNombre(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Género no encontrado con nombre: " + nombre));
    }

    // 4. CREAR/ACTUALIZAR
    @PostMapping
    public ResponseEntity<Genero> crear(@Valid @RequestBody Genero genero) {
        // @Valid ahora interceptará si el nombre está vacío o es muy corto antes de entrar aquí
        return ResponseEntity.ok(service.guardar(genero));
    }

    // 5. ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.buscarPorId(id).isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResourceNotFoundException("No se puede eliminar: Género no encontrado con ID: " + id);
    }
}
