package juego.modelo;

import java.io.Serializable;
import java.util.List;

public interface TipoJuego extends Serializable {
    // Define el contrato  para cualquier combinacion válida
    // Valida si el conjunto de cartas almacenado en la clase concreta
     // (Trio, Escalera) cumple con la regla de combinación.
    boolean juegoValido();

    List<Carta> getCartas();
}
