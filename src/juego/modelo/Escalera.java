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

    public static List<Escalera> encontrarEn(List<Carta> mano){
        // Agrupo todas las cartas de la mano por palo para buscar escaleras,
        // ya que una escalera debe ser del mismo palo.
        Map<Palo, List<Carta>> porPalo = new HashMap<>();
        for (Carta carta : mano) { // uso mano
            porPalo.computeIfAbsent(carta.getPalo(), k -> new ArrayList<>()).add(carta);
        }

        // devolvera objetos Escalera
        List<Escalera> escalasValidas = new ArrayList<>();
        // itero sobre cada palo
        for (List<Carta> grupo : porPalo.values()) {
            // uso TreeSet para obtener solo los numeros unicos y ordenados del palo
            Set<Integer> numerosUnicos = new TreeSet<>();
            for (Carta c : grupo)
                numerosUnicos.add(c.getNumero());
            List<Integer> listaNumeros = new ArrayList<>(numerosUnicos);

            for (int i = 0; i < listaNumeros.size(); i++) {
                List<Carta> secuencia = new ArrayList<>();
                // Comienza a iterar desde el número actual para formar una secuencia
                for (int j = i; j < listaNumeros.size(); j++) {
                    // // se rompe si el numero actual no es el consecutivo del anterior
                    if (j > i && listaNumeros.get(j) != listaNumeros.get(j - 1) + 1)
                        break;
                    int num = listaNumeros.get(j);
                    //busca la carta REAL que corresponde a este 'num' y que aún no ha sido usada en esta secuencia.
                    Optional<Carta> opcion = grupo.stream()
                            .filter(c -> c.getNumero() == num && !secuencia.contains(c))
                            .findFirst();
                    if (!opcion.isPresent())
                        break;
                    secuencia.add(opcion.get());

                    // si se encuentra una secuencia valida (>= 3), se crea un objeto Escalera
                    if (secuencia.size() >= 3) {
                        // crea y guarda el objeto Escalera, no una lista de listas
                        escalasValidas.add(new Escalera(new ArrayList<>(secuencia)));
                    }
                }
            }
        }
        return escalasValidas;
    }

    @Override
    public List<Carta> getCartas() {
        return cartas;
    }

    @Override
    public boolean juegoValido() {
        if (cartas.size() < 3) return false;

        Palo primerPalo = cartas.get(0).getPalo();

        // verifica mismo palo y consecutividad
        for (int i = 0; i < cartas.size(); i++) {
            // la logica debe manejar el comodin (Palo.COMODIN)
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
