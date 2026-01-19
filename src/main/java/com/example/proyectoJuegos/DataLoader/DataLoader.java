package com.example.proyectoJuegos.DataLoader;

import com.example.proyectoJuegos.Entities.*;
import com.example.proyectoJuegos.Enums.Estado;
import com.example.proyectoJuegos.Services.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final JuegoService juegoService;
    private final UserGameService userGameService;
    private final GeneroService generoService;
    private final ReviewService reviewService;
    private final PasswordEncoder passwordEncoder;

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
        Genero rpg = crearGenero("RPG");
        Genero accion = crearGenero("Acción");
        Genero aventura = crearGenero("Aventura");
        Genero shooter = crearGenero("Shooter");
        Genero terror = crearGenero("Terror");
        Genero plataforma = crearGenero("Plataformas");

        // 2. Crear Usuarios
        Usuario fran = crearUsuario("Fran", "fran@example.com", "1234");
        Usuario maria = crearUsuario("Maria", "maria@gmail.com", "1234");
        Usuario admin = crearUsuario("Admin", "admin@juegos.com", "admin");

        // 3. Crear el catálogo de 30 juegos
        List<Juego> juegos = new ArrayList<>();

        // --- RPG ---
        juegos.add(crearJuego("The Witcher 3", "Caza monstruos en un mundo abierto épico.", 2015, 5, 19, rpg));
        juegos.add(crearJuego("Elden Ring", "Desafío extremo en las Tierras Intermedias.", 2022, 2, 25, rpg));
        juegos.add(crearJuego("Cyberpunk 2077", "Futuro distópico en Night City.", 2020, 12, 10, rpg));
        juegos.add(crearJuego("Baldur's Gate 3", "Rol puro basado en Dungeons & Dragons.", 2023, 8, 3, rpg));
        juegos.add(crearJuego("Final Fantasy VII Rebirth", "El viaje de Cloud continúa fuera de Midgar.", 2024, 2, 29, rpg));
        juegos.add(crearJuego("Skyrim", "Exploración infinita en las tierras del norte.", 2011, 11, 11, rpg));

        // --- ACCIÓN / AVENTURA ---
        juegos.add(crearJuego("God of War Ragnarok", "Kratos y Atreus enfrentan el fin del mundo.", 2022, 11, 9, accion));
        juegos.add(crearJuego("Red Dead Redemption 2", "La vida de un forajido en el ocaso del salvaje oeste.", 2018, 10, 26, aventura));
        juegos.add(crearJuego("Zelda: Tears of the Kingdom", "Libertad creativa total en el reino de Hyrule.", 2023, 5, 12, aventura));
        juegos.add(crearJuego("The Last of Us Part II", "Una cruda historia de venganza y redención.", 2020, 6, 19, aventura));
        juegos.add(crearJuego("Ghost of Tsushima", "Un samurái contra la invasión mongola.", 2020, 7, 17, accion));
        juegos.add(crearJuego("Horizon Forbidden West", "Aloy viaja al oeste prohibido.", 2022, 2, 18, aventura));
        juegos.add(crearJuego("Spider-Man 2", "Peter y Miles contra Kraven y Venom.", 2023, 10, 20, accion));
        juegos.add(crearJuego("Uncharted 4", "La última aventura de Nathan Drake.", 2016, 5, 10, aventura));

        // --- SHOOTER ---
        juegos.add(crearJuego("DOOM Eternal", "Matanza frenética de demonios al ritmo de metal.", 2020, 3, 20, shooter));
        juegos.add(crearJuego("Halo Infinite", "El Jefe Maestro regresa a un anillo Halo.", 2021, 12, 8, shooter));
        juegos.add(crearJuego("Overwatch 2", "Héroes compitiendo en partidas por equipos.", 2022, 10, 4, shooter));
        juegos.add(crearJuego("Call of Duty: MW3", "Combate táctico militar intenso.", 2023, 11, 10, shooter));
        juegos.add(crearJuego("BioShock Infinite", "Aventura en la ciudad flotante de Columbia.", 2013, 3, 26, shooter));

        // --- TERROR ---
        juegos.add(crearJuego("Resident Evil 4 Remake", "Leon Kennedy rescata a la hija del presidente.", 2023, 3, 24, terror));
        juegos.add(crearJuego("Dead Space Remake", "Terror claustrofóbico en la nave Ishimura.", 2023, 1, 27, terror));
        juegos.add(crearJuego("Silent Hill 2", "Un clásico del terror psicológico.", 2001, 9, 24, terror));
        juegos.add(crearJuego("Alan Wake 2", "Escritor atrapado en una pesadilla real.", 2023, 10, 27, terror));
        juegos.add(crearJuego("Outlast", "Sobrevive con una cámara en un manicomio.", 2013, 9, 4, terror));

        // --- PLATAFORMAS / OTROS ---
        juegos.add(crearJuego("Hollow Knight", "Explora un reino de insectos bellamente dibujado.", 2017, 2, 24, aventura));
        juegos.add(crearJuego("It Takes Two", "Cooperación necesaria para salvar un matrimonio.", 2021, 3, 26, plataforma));
        juegos.add(crearJuego("Super Mario Odyssey", "Viaja por mundos increíbles con Cappy.", 2017, 10, 27, plataforma));
        juegos.add(crearJuego("Cuphead", "Estética de dibujos de los años 30 y alta dificultad.", 2017, 9, 29, plataforma));
        juegos.add(crearJuego("Stray", "Explora una ciudad ciberpunk siendo un gato.", 2022, 7, 19, aventura));
        juegos.add(crearJuego("Sekiro: Shadows Die Twice", "Combate de katanas preciso y exigente.", 2019, 3, 22, accion));

        // 4. Crear Reseñas Aleatorias para que el catálogo tenga vida
        String[] comentarios = {"Obra maestra", "Me aburrió un poco", "Increíble apartado gráfico", "Jugabilidad perfecta", "Lo recomiendo totalmente", "Un poco difícil pero vale la pena"};
        Random random = new Random();

        for (Juego juego : juegos) {
            Review r = new Review();
            r.setJuego(juego);
            r.setAutor(fran);
            r.setRating(random.nextInt(3, 6)); // Notas de 3 a 5
            r.setComentario(comentarios[random.nextInt(comentarios.length)]);
            reviewService.guardar(r);
        }

        System.out.println(">> ¡Base de datos cargada con 30 juegos y reseñas! Ya puedes probar la interfaz.");
    }

    // --- MÉTODOS AUXILIARES PARA LIMPIAR EL CÓDIGO ---

    private Genero crearGenero(String nombre) {
        Genero g = new Genero();
        g.setNombre(nombre);
        return generoService.guardar(g);
    }

    private Usuario crearUsuario(String nombre, String email, String pass) {
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(pass));
        return usuarioService.guardar(u);
    }

    private Juego crearJuego(String titulo, String desc, int year, int month, int day, Genero genero) {
        Juego j = new Juego();
        j.setTitulo(titulo);
        j.setDescripcion(desc);
        j.setFechaSalida(LocalDate.of(year, month, day));
        j.getGeneros().add(genero);
        return juegoService.guardar(j);
    }
}