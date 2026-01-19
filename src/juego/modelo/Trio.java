package juego.modelo;

import java.io.Serializable;
import java.util.*;

public class Trio implements TipoJuego, Serializable {

    private final List<Carta> cartas;

    // Constructor: Recibe las cartas que forman esta combinacion especifica
    public Trio(List<Carta> cartas) {
        this.cartas = new ArrayList<>(cartas);
    }

    public static List<Trio> encontrarEn(List<Carta> mano){
        // uso un Map para agrupar todas las cartas por su numero
        Map<Integer, List<Carta>> porNumero = new HashMap<>();
        for (Carta carta : mano) { // USAR 'mano'
            porNumero.computeIfAbsent(carta.getNumero(), k -> new ArrayList<>()).add(carta);
        }

        // cambie el tipo de retorno devolvera objetos Trio
        List<Trio> triosValidos = new ArrayList<>();
        //Iteraro sobre cada grupo de numeros iguales
        for (Map.Entry<Integer, List<Carta>> entrada : porNumero.entrySet()) {
            List<Carta> grupo = entrada.getValue();
            if (grupo.size() >= 3) { //solo sigo si hay almenos 3 cartas del mismo palo

                // usar combinar para obtener todas las posibles combinaciones de 3
                List<List<Carta>> combinacionesDeTres = combinar(grupo, 3);
                for (List<Carta> listaCartas : combinacionesDeTres) {
                    triosValidos.add(new Trio(listaCartas)); // Crea y guarda el objeto Trio
                }

                // si hay 4 cartas, tambien es una combinacion valida
                if (grupo.size() == 4) {
                    triosValidos.add(new Trio(new ArrayList<>(grupo))); // Crea el objeto Trio (cuarteto)
                }
            }
        }
        return triosValidos;
    }

    private  static List<List<Carta>> combinar(List<Carta> elementos, int n) {
        if (n > elementos.size())
            return Collections.emptyList();
        if (n == 1) {
            List<List<Carta>> individuales = new ArrayList<>();
            for (Carta c : elementos) {
                individuales.add(Arrays.asList(c));
            }
            return individuales;
        }

        List<List<Carta>> combinaciones = new ArrayList<>();
        for (int i = 0; i <= elementos.size() - n; i++) {
            Carta cabeza = elementos.get(i);
            List<Carta> cola = elementos.subList(i + 1, elementos.size());
            for (List<Carta> sub : combinar(cola, n - 1)) {
                List<Carta> nueva = new ArrayList<>();
                nueva.add(cabeza);
                nueva.addAll(sub);
                combinaciones.add(nueva);
            }
        }
        return combinaciones;
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
