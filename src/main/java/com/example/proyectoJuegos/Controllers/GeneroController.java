package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.Genero;
import com.example.proyectoJuegos.Services.GeneroService;
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

    // 1. LISTAR TODOS: GET http://localhost:8081/api/generos
    @GetMapping
    public List<Genero> obtenerTodos() {
        return service.listarTodos();
    }

    // 2. BUSCAR POR ID: GET http://localhost:8081/api/generos/1
    @GetMapping("/{id}")
    public ResponseEntity<Genero> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. BUSCAR POR NOMBRE: GET http://localhost:8081/api/generos/buscar?nombre=rpg
    @GetMapping("/buscar")
    public ResponseEntity<Genero> obtenerPorNombre(@RequestParam String nombre) {
        return service.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. CREAR/ACTUALIZAR: POST http://localhost:8081/api/generos
    @PostMapping
    public ResponseEntity<Genero> crear(@RequestBody Genero genero) {
        // Al guardar, el servicio lo pondrá en MAYÚSCULAS automáticamente
        return ResponseEntity.ok(service.guardar(genero));
    }

    // 5. ELIMINAR: DELETE http://localhost:8081/api/generos/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.buscarPorId(id).isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
