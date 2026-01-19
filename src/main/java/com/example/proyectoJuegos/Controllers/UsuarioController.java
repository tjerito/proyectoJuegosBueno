package com.example.proyectoJuegos.Controllers;

import com.example.proyectoJuegos.Entities.Usuario;
import com.example.proyectoJuegos.Exceptions.ResourceNotFoundException;
import com.example.proyectoJuegos.Services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    // 1. OBTENER TODOS
    @GetMapping
    public List<Usuario> obtenerTodos() {
        return service.listarTodos();
    }

    // 2. OBTENER POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Long id) {
        Usuario usuario = service.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return ResponseEntity.ok(usuario);
    }

    // 3. BUSCAR POR EMAIL
    @GetMapping("/email")
    public ResponseEntity<Usuario> obtenerPorEmail(@RequestParam("valor") String email) {
        Usuario usuario = service.buscarPorEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un usuario registrado con el email: " + email));
        return ResponseEntity.ok(usuario);
    }

    // 4. CREAR USUARIO
    @PostMapping
    public ResponseEntity<Usuario> crear(@Valid @RequestBody Usuario usuario) {
        // La validación de email duplicado sigue siendo lógica de negocio necesaria
        if (service.existeEmail(usuario.getEmail())) {
            // Podrías crear una excepción personalizada "DuplicateResourceException" si quisieras
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(usuario));
    }

    // 5. ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(u -> {
                    service.eliminar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: Usuario no encontrado con ID: " + id));
    }
}
