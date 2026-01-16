package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController combina dos etiquetas @Controller y @ResponseBody
//Dice a Spring que este componente es el encargado de recibir peticiones HTTP
@RestController
//@RequestMapping es la ruta base del controlador
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // 1. OBTENER TODOS: GET http://localhost:8081/api/usuarios
    @GetMapping
    public List<Usuario> obtenerTodos() {
        //Internamente Spring toma la lista y devuelve un paquete como los demás métodos
        //Sin embargo, al no tener que controlar ninguna situación concreta, podemos hacer un return de una lista
        return service.listarTodos();
    }

    // 2. OBTENER POR ID: GET http://localhost:8081/api/usuarios/1
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 3. BUSCAR POR EMAIL: GET http://localhost:8081/api/usuarios/email?valor=fran@example.com
    @GetMapping("/email")
    public ResponseEntity<Usuario> obtenerPorEmail(@RequestParam("valor") String email) {
        return service.buscarPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. CREAR USUARIO: POST http://localhost:8081/api/usuarios
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        // Validación: No permitir emails duplicados
        if (service.existeEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest().body("Error: El email ya está registrado.");
        }
        return ResponseEntity.ok(service.guardar(usuario));
    }

    // 5. ELIMINAR: DELETE http://localhost:8081/api/usuarios/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.buscarPorId(id).isPresent()) {
            service.eliminar(id);
            return ResponseEntity.noContent().build(); // Devuelve 204 (Éxito, sin contenido)
        }
        return ResponseEntity.notFound().build();
    }

}
