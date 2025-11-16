package juego.modelo;

import java.io.Serializable;
import java.util.*;

public class Escalera implements TipoJuego, Serializable {

    private final List<Carta> cartas;

    public Escalera(List<Carta> cartas) {
        // Almacenamos y ordenamos las cartas para facilitar la validación
        this.cartas = cartas.stream()
                .sorted(Comparator.comparing(Carta::getNumero))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public List<Carta> getCartas() {
        return cartas;
    }

    @Override
    public boolean juegoValido() {
        if (cartas.size() < 3) return false;

        Palo primerPalo = cartas.get(0).getPalo();

        // 1. Verifica mismo palo y consecutividad
        for (int i = 0; i < cartas.size(); i++) {
            // Nota: Aquí la lógica real debe manejar el comodín (Palo.COMODIN)
            if (cartas.get(i).getPalo() != primerPalo) return false;

            if (i > 0) {
                if (cartas.get(i).getNumero() != cartas.get(i - 1).getNumero() + 1) {
                    return false;
                }
            }
        }
        return true;
    }

}
