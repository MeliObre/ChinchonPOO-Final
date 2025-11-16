package juego.modelo;

import java.io.Serializable;
import java.util.List;

public interface TipoJuego extends Serializable {
    boolean juegoValido();

    List<Carta> getCartas();
}
