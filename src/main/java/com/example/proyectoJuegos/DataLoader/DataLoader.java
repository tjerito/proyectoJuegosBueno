package com.example.proyectoJuegos.DataLoader;

import com.example.proyectoJuegos.Entities.*;
import com.example.proyectoJuegos.Enums.Estado;
import com.example.proyectoJuegos.Services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

//La clase DataLoader es la encargada de crear datos de prueba cuando la aplicación se inicie

@Component
//Al implementar esta interfaz le estamos diciendo a Spring que cuando cargue todos los servicios y este todo operativo
//ejecute el metodo que implementa la interfaz. Este método es "run()"
public class DataLoader implements CommandLineRunner {

    //Creamos e inyectamos los servicios en el controlador
    private final UsuarioService usuarioService;
    private final JuegoService juegoService;
    private final UserGameService userGameService;
    private final GeneroService generoService;
    private final ReviewService reviewService;


    public DataLoader(UsuarioService uS, JuegoService jS, UserGameService ugS, GeneroService gS, ReviewService rS) {
        this.usuarioService = uS;
        this.juegoService = jS;
        this.userGameService = ugS;
        this.generoService = gS;
        this.reviewService = rS;
    }

    //Este es el método encargado de crear los datos de prueba una vez todo esté operativo
    @Override
    public void run(String... args) throws Exception {
        // 1. Crear Géneros
        Genero rpg = new Genero();
        rpg.setNombre("RPG");
        generoService.guardar(rpg);

        // 2. Crear un Juego
        Juego witcher = new Juego();
        witcher.setTitulo("The Witcher 3");
        witcher.setFechaSalida(LocalDate.of(2015, 5, 19));
        witcher.getGeneros().add(rpg);
        juegoService.guardar(witcher);

        // 3. Crear un Usuario
        Usuario user = new Usuario();
        user.setNombre("Fran");
        user.setEmail("fran@example.com");
        usuarioService.guardar(user);

        // 4. Unir Usuario con Juego (UserGame)
        UserGame progreso = new UserGame();
        progreso.setUser(user);
        progreso.setGame(witcher);
        progreso.setHorasJugadas(10);
        progreso.setEstado(Estado.JUGANDO);
        userGameService.guardarProgreso(progreso);

        // 5. Crear una Review (La que faltaba)
        Review reseña = new Review();
        reseña.setComentario("Es el mejor RPG que he jugado en mi vida.");
        reseña.setRating(10);
        reseña.setAutor(user); // Conectamos con el usuario
        reseña.setJuego(witcher); // Conectamos con el juego
        // La fecha se pondrá automáticamente en el Service si lo programamos así
        reviewService.guardar(reseña);

        System.out.println(">> Base de datos cargada con éxito (incluyendo reviews).");
    }

}
