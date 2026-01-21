package com.example.proyectoJuegos.Services;

import com.example.proyectoJuegos.Entities.UserGame;
import com.example.proyectoJuegos.Enums.Estado;
import com.example.proyectoJuegos.Repositories.UserGameRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserGameService {

    private final UserGameRepositorio repo;

    public UserGameService(UserGameRepositorio repo) {
        this.repo = repo;
    }


    public UserGame guardarProgreso(UserGame userGame) {
        if (userGame.getHorasJugadas() < 0) {
            userGame.setHorasJugadas(0);
        }
        return repo.save(userGame);
    }

    public List<UserGame> obtenerBibliotecaPorUsuario(Long userId) {
        return repo.findByUserId(userId);
    }



    public UserGame añadirHoras(Long userId, Long gameId, int nuevasHoras) {
        Optional<UserGame> registro = repo.findByUserIdAndGameId(userId, gameId);

        if (registro.isPresent()) {
            UserGame ug = registro.get();
            ug.setHorasJugadas(ug.getHorasJugadas() + nuevasHoras);
            return repo.save(ug);
        }
        return null;
    }

    public UserGame cambiarEstado(Long userId, Long gameId, Estado nuevoEstado) {
        return repo.findByUserIdAndGameId(userId, gameId)
                .map(ug -> {
                    ug.setEstado(nuevoEstado);
                    return repo.save(ug);
                })
                .orElse(null);
    }

    public List<UserGame> buscarPorEstado(Estado estado) {
        return repo.findByEstado(estado);
    }

    public List<UserGame> buscarJuegosMuyJugados(Integer horas) {
        return repo.findByHorasJugadasGreaterThan(horas);
    }

    public void eliminarDeBiblioteca(Long id) {
        repo.deleteById(id);
    }

}
