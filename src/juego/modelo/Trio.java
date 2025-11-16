package juego.modelo;

import java.io.Serializable;
import java.util.*;

public class Trio implements TipoJuego, Serializable {

    private final List<Carta> cartas;

    // Constructor: Recibe las cartas que forman esta combinación específica
    public Trio(List<Carta> cartas) {
        this.cartas = new ArrayList<>(cartas);
    }

    @Override
    public List<Carta> getCartas() {
        return cartas;
    }

    @Override
    public boolean juegoValido() {
        // Lógica para validar que estas cartas realmente forman un trío.
        if (cartas.size() < 3 || cartas.size() > 4) return false;

        // Se asume que el primer número es el número del trío
        int primerNumero = cartas.get(0).getNumero();
        for (Carta carta : cartas) {
            if (carta.getNumero() != primerNumero) {
                // Si encontramos alguna carta con número diferente, no es trío
                return false;
            }
        }
        return true;
    }

}
