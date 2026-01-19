package com.example.proyectoJuegos.DataLoader;

import com.example.proyectoJuegos.Entities.*;
import com.example.proyectoJuegos.Enums.Estado;
import com.example.proyectoJuegos.Services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

//La clase DataLoader es la encargada de crear datos de prueba cuando la aplicación se inicie

@Component
//Al implementar esta interfaz le estamos diciendo a Spring que cuando cargue todos los servicios y este todo operativo
//ejecute el metodo que implementa la interfaz. Este método es "run()"
public class DataLoader implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final JuegoService juegoService;
    private final UserGameService userGameService;
    private final GeneroService generoService;
    private final ReviewService reviewService;
    private final PasswordEncoder passwordEncoder; // Para que el login funcione

    public DataLoader(UsuarioService uS, JuegoService jS, UserGameService ugS,
                      GeneroService gS, ReviewService rS, PasswordEncoder pE) {
        this.usuarioService = uS;
        this.juegoService = jS;
        this.userGameService = ugS;
        this.generoService = gS;
        this.reviewService = rS;
        this.passwordEncoder = pE;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Crear Géneros
        Genero rpg = new Genero();
        rpg.setNombre("RPG");
        generoService.guardar(rpg);

        // 2. Crear un Juego (Añadida la descripción para evitar el error de validación)
        Juego witcher = new Juego();
        witcher.setTitulo("The Witcher 3");
        witcher.setDescripcion("Una aventura épica en un mundo abierto lleno de monstruos."); // SOLUCIÓN AL ERROR
        witcher.setFechaSalida(LocalDate.of(2015, 5, 19));
        witcher.getGeneros().add(rpg);
        juegoService.guardar(witcher);

        // 3. Crear un Usuario (Añadida contraseña cifrada para Spring Security)
        Usuario user = new Usuario();
        user.setNombre("Fran");
        user.setEmail("fran@example.com");
        user.setPassword(passwordEncoder.encode("1234")); // Ciframos la pass
        usuarioService.guardar(user);

        // 4. Unir Usuario con Juego (UserGame)
        UserGame progreso = new UserGame();
        progreso.setUser(user);
        progreso.setGame(witcher);
        progreso.setHorasJugadas(10);
        progreso.setEstado(Estado.JUGANDO);
        userGameService.guardarProgreso(progreso);

        // 5. Crear una Review
        Review reseña = new Review();
        reseña.setComentario("Es el mejor RPG que he jugado en mi vida.");
        reseña.setRating(4);
        reseña.setAutor(user);
        reseña.setJuego(witcher);
        reviewService.guardar(reseña);

        System.out.println(">> Base de datos cargada con éxito (incluyendo descripciones y seguridad).");
    }

}
