package juego.modelo;

import java.io.Serializable;
import java.util.*;

public class Trio implements TipoJuego, Serializable {

    private final List<Carta> cartas;

    // Constructor: Recibe las cartas que forman esta combinacion especifica
    public Trio(List<Carta> cartas) {
        this.cartas = new ArrayList<>(cartas);
    }

    @Override
    public List<Carta> getCartas() {
        return cartas;
    }

    @Override
    public boolean juegoValido() {

        if (cartas.size() < 3 || cartas.size() > 4) return false;

        // Asumo que el primer numero es el numero del trio
        int primerNumero = cartas.get(0).getNumero();
        for (Carta carta : cartas) {
            if (carta.getNumero() != primerNumero) {
                // Si encontramos alguna carta con numero diferente, no es trio
                return false;
            }
        }
        return true;
    }

}
