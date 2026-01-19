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

        // 3. Crear el catálogo de 30 juegos con el atributo urlImagen
        List<Juego> juegos = new ArrayList<>();

        // --- RPG ---
        juegos.add(crearJuego("The Witcher 3", "Caza monstruos en un mundo abierto épico.", 2015, 5, 19, rpg, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/292030/header.jpg"));
        juegos.add(crearJuego("Elden Ring", "Desafío extremo en las Tierras Intermedias.", 2022, 2, 25, rpg, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1245620/header.jpg"));
        juegos.add(crearJuego("Cyberpunk 2077", "Futuro distópico en Night City.", 2020, 12, 10, rpg, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1091500/header.jpg"));
        juegos.add(crearJuego("Baldur's Gate 3", "Rol puro basado en Dungeons & Dragons.", 2023, 8, 3, rpg, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1086940/header.jpg"));
        juegos.add(crearJuego("Final Fantasy VII Rebirth", "El viaje de Cloud continúa fuera de Midgar.", 2024, 2, 29, rpg, "https://image.api.playstation.com/vulcan/ap/rnd/202309/0712/9160100d3d528b7e289895c1a7d6e64ca658392cf99a19c5.png"));
        juegos.add(crearJuego("Skyrim", "Exploración infinita en las tierras del norte.", 2011, 11, 11, rpg, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/489830/header.jpg"));

        // --- ACCIÓN / AVENTURA ---
        juegos.add(crearJuego("God of War Ragnarok", "Kratos y Atreus enfrentan el fin del mundo.", 2022, 11, 9, accion, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2322010/header.jpg"));
        juegos.add(crearJuego("Red Dead Redemption 2", "La vida de un forajido en el ocaso del salvaje oeste.", 2018, 10, 26, aventura, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1174180/header.jpg"));
        juegos.add(crearJuego("Zelda: Tears of the Kingdom", "Libertad creativa total en el reino de Hyrule.", 2023, 5, 12, aventura, "https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_656/bcom/en_US/games/switch/t/the-legend-of-zelda-tears-of-the-kingdom-switch/hero"));
        juegos.add(crearJuego("The Last of Us Part II", "Una cruda historia de venganza y redención.", 2020, 6, 19, aventura, "https://image.api.playstation.com/vulcan/img/rnd/202010/2618/it98up39YpS69Yp9v9v9v9v9.png"));
        juegos.add(crearJuego("Ghost of Tsushima", "Un samurái contra la invasión mongola.", 2020, 7, 17, accion, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2215430/header.jpg"));
        juegos.add(crearJuego("Horizon Forbidden West", "Aloy viaja al oeste prohibido.", 2022, 2, 18, aventura, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2420110/header.jpg"));
        juegos.add(crearJuego("Spider-Man 2", "Peter y Miles contra Kraven y Venom.", 2023, 10, 20, accion, "https://image.api.playstation.com/vulcan/ap/rnd/202306/1219/60132d0575660361597f8af92a71ad927e57962cf4ca071a.png"));
        juegos.add(crearJuego("Uncharted 4", "La última aventura de Nathan Drake.", 2016, 5, 10, aventura, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1659420/header.jpg"));

        // --- SHOOTER ---
        juegos.add(crearJuego("DOOM Eternal", "Matanza frenética de demonios al ritmo de metal.", 2020, 3, 20, shooter, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/782330/header.jpg"));
        juegos.add(crearJuego("Halo Infinite", "El Jefe Maestro regresa a un anillo Halo.", 2021, 12, 8, shooter, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1240440/header.jpg"));
        juegos.add(crearJuego("Overwatch 2", "Héroes compitiendo en partidas por equipos.", 2022, 10, 4, shooter, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1506830/header.jpg"));
        juegos.add(crearJuego("Call of Duty: MW3", "Combate táctico militar intenso.", 2023, 11, 10, shooter, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2314390/header.jpg"));
        juegos.add(crearJuego("BioShock Infinite", "Aventura en la ciudad flotante de Columbia.", 2013, 3, 26, shooter, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/8870/header.jpg"));

        // --- TERROR ---
        juegos.add(crearJuego("Resident Evil 4 Remake", "Leon Kennedy rescata a la hija del presidente.", 2023, 3, 24, terror, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2050650/header.jpg"));
        juegos.add(crearJuego("Dead Space Remake", "Terror claustrofóbico en la nave Ishimura.", 2023, 1, 27, terror, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1693980/header.jpg"));
        juegos.add(crearJuego("Silent Hill 2", "Un clásico del terror psicológico.", 2001, 9, 24, terror, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/2124490/header.jpg"));
        juegos.add(crearJuego("Alan Wake 2", "Escritor atrapado en una pesadilla real.", 2023, 10, 27, terror, "https://image.api.playstation.com/vulcan/ap/rnd/202305/1711/846875b22b07e77b61c9255ef81e9f733f11409f53e6b206.png"));
        juegos.add(crearJuego("Outlast", "Sobrevive con una cámara en un manicomio.", 2013, 9, 4, terror, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/238320/header.jpg"));

        // --- PLATAFORMAS / OTROS ---
        juegos.add(crearJuego("Hollow Knight", "Explora un reino de insectos bellamente dibujado.", 2017, 2, 24, aventura, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/367520/header.jpg"));
        juegos.add(crearJuego("It Takes Two", "Cooperación necesaria para salvar un matrimonio.", 2021, 3, 26, plataforma, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1426210/header.jpg"));
        juegos.add(crearJuego("Super Mario Odyssey", "Viaja por mundos increíbles con Cappy.", 2017, 10, 27, plataforma, "https://assets.nintendo.com/image/upload/ar_16:9,c_lpad,w_656/bcom/en_US/games/switch/s/super-mario-odyssey-switch/hero"));
        juegos.add(crearJuego("Cuphead", "Estética de dibujos de los años 30.", 2017, 9, 29, plataforma, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/268910/header.jpg"));
        juegos.add(crearJuego("Stray", "Explora una ciudad ciberpunk siendo un gato.", 2022, 7, 19, aventura, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/1332010/header.jpg"));
        juegos.add(crearJuego("Sekiro", "Combate de katanas preciso y exigente.", 2019, 3, 22, accion, "https://shared.fastly.steamstatic.com/store_item_assets/steam/apps/814380/header.jpg"));

        // 4. Crear Reseñas Aleatorias
        String[] comentarios = {"Obra maestra", "Me aburrió un poco", "Increíble apartado gráfico", "Jugabilidad perfecta", "Lo recomiendo totalmente"};
        Random random = new Random();

        for (Juego juego : juegos) {
            Review r = new Review();
            r.setJuego(juego);
            r.setAutor(fran);
            r.setRating(random.nextInt(3, 6));
            r.setComentario(comentarios[random.nextInt(comentarios.length)]);
            reviewService.guardar(r);
        }

        System.out.println(">> ¡Base de datos cargada con 30 juegos e imágenes!");
    }

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

    // --- MÉTODO AUXILIAR ACTUALIZADO CON urlImagen ---
    private Juego crearJuego(String titulo, String desc, int year, int month, int day, Genero genero, String url) {
        Juego j = new Juego();
        j.setTitulo(titulo);
        j.setDescripcion(desc);
        j.setFechaSalida(LocalDate.of(year, month, day));
        j.setUrlImagen(url); // SE ASIGNA AQUÍ
        j.getGeneros().add(genero);
        return juegoService.guardar(j);
    }
}