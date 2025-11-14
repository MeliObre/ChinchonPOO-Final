package juego.servicios;
import juego.modelo.Carta;
import juego.modelo.Palo;
import juego.modelo.ResultCierre;
import juego.modelo.Mano;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import java.util.*;

public class ComprobarMano {
    public static boolean esListaCerrable (List<Carta> mano){
        //solo para el testeo
        if (mano.size() !=7)
             return false;
        List<List<Carta>> combinacionesPosibles = new ArrayList<>();
        combinacionesPosibles.addAll(encontrarTrio(mano));
        combinacionesPosibles.addAll((encontrarEscaleras(mano)));
        if (buscarCombinacion(combinacionesPosibles, 7,new ArrayList<>(),0) != null){
            return true;
        }
        List<Carta> usadaSeis = buscarCombinacion(combinacionesPosibles, 6 , new ArrayList<>(), 0);
        if (usadaSeis != null)
            return true;
        return false;
    }

    public static ResultCierre esManoCerrable (List<Carta> mano){
        ResultCierre resultCierreRecord = null ; //por default devuelve falso

        for (int i = 0 ; i < mano.size(); i++){
            //copia de la mano sin la carta actual
            List<Carta> copia = new ArrayList<>(mano);
            Carta cartaCierre = mano.get(i);
            copia.remove(i);

            if (esListaCerrable(copia)){
                // si una copia cumple la codicion la mano es cerrable
                Mano manoCerrableActual = Mano.arrayToMano(copia);
                if (resultCierreRecord == null){
                    resultCierreRecord = new ResultCierre(true, cartaCierre, manoCerrableActual);
                    continue;
                }
                int puntajeRecord = resultCierreRecord.mano.calcularPuntajeRestante();

                if (manoCerrableActual.calcularPuntajeRestante() < puntajeRecord) {
                    // si no hay ninguna mano cerrable o si la mano cerrable actual da puntos más
                    // bajos que la guardada, actualizo el resultado
                    resultCierreRecord = new ResultCierre(true, cartaCierre, manoCerrableActual);
                }
            }
        }
        if (resultCierreRecord == null)
            return new ResultCierre(false, null, null);

        return resultCierreRecord;
    }

    private static List<List<Carta>> encontrarTrio(List<Carta> cartas) {
        Map<Integer, List<Carta>> porNumero = new HashMap<>();
        for (Carta carta : cartas) {
            porNumero.computeIfAbsent(carta.getNumero(), k -> new ArrayList<>()).add(carta);
        }

        List<List<Carta>> grupos = new ArrayList<>();
        for (Map.Entry<Integer, List<Carta>> entrada : porNumero.entrySet()) {
            List<Carta> grupo = entrada.getValue();
            if (grupo.size() >= 3) {
                grupos.addAll(combinar(grupo, 3));
                if (grupo.size() == 4) {
                    grupos.add(new ArrayList<>(grupo));
                }
            }
        }
        return grupos;
    }

    private static List<List<Carta>> encontrarEscaleras(List<Carta> cartas) {
        Map<Palo, List<Carta>> porPalo = new HashMap<>();
        for (Carta carta : cartas) {
            porPalo.computeIfAbsent(carta.getPalo(), k -> new ArrayList<>()).add(carta);
        }

        List<List<Carta>> escalas = new ArrayList<>();
        for (List<Carta> grupo : porPalo.values()) {
            Set<Integer> numerosUnicos = new TreeSet<>();
            for (Carta c : grupo)
                numerosUnicos.add(c.getNumero());
            List<Integer> listaNumeros = new ArrayList<>(numerosUnicos);

            for (int i = 0; i < listaNumeros.size(); i++) {
                List<Carta> secuencia = new ArrayList<>();
                for (int j = i; j < listaNumeros.size(); j++) {
                    if (j > i && listaNumeros.get(j) != listaNumeros.get(j - 1) + 1)
                        break;
                    int num = listaNumeros.get(j);
                    Optional<Carta> opcion = grupo.stream()
                            .filter(c -> c.getNumero() == num && !secuencia.contains(c))
                            .findFirst();
                    if (!opcion.isPresent())
                        break;
                    secuencia.add(opcion.get());
                    if (secuencia.size() >= 3) {
                        escalas.add(new ArrayList<>(secuencia));
                    }
                }
            }
        }
        return escalas;
    }

    private static List<List<Carta>> combinar(List<Carta> elementos, int n) {
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

    private static List<Carta> buscarCombinacion(List<List<Carta>> grupos, int objetivo, List<Carta> usadas,
                                                 int indiceInicial) {
        // Ordenar por tamaño descendente para priorizar escaleras/cuartetos más grandes
        if (indiceInicial == 0) {
            grupos.sort((a, b) -> b.size() - a.size());
        }

        if (usadas.size() == objetivo)
            return usadas;
        if (indiceInicial >= grupos.size() || usadas.size() > objetivo)
            return null;

        for (int i = indiceInicial; i < grupos.size(); i++) {
            List<Carta> grupo = grupos.get(i);

            // Si cualquier carta del grupo ya está en 'usadas', descartamos este grupo
            boolean haySolapamiento = grupo.stream().anyMatch(usadas::contains);
            if (haySolapamiento)
                continue;

            if (usadas.size() + grupo.size() > objetivo)
                continue;

            List<Carta> combinadas = new ArrayList<>(usadas);
            combinadas.addAll(grupo);
            List<Carta> resultado = buscarCombinacion(grupos, objetivo, combinadas, i + 1);
            if (resultado != null)
                return resultado;
        }
        return null;
    }

    public static List<Carta> cartasNoUsadas(List<Carta> mano) {
        List<List<Carta>> combinacionesPosibles = new ArrayList<>();
        combinacionesPosibles.addAll(encontrarTrio(mano));
        combinacionesPosibles.addAll(encontrarEscaleras(mano));

        List<Carta> cartasUsadas = null;
        // Buscamos la combinación más larga >= 3 cartas
        for (int objetivo = mano.size(); objetivo >= 3; objetivo--) {
            cartasUsadas = buscarCombinacion(combinacionesPosibles, objetivo, new ArrayList<>(), 0);
            if (cartasUsadas != null) {
                break;
            }
        }

        if (cartasUsadas != null) {
            List<Carta> cartasNoUsadas = new ArrayList<>(mano);
            cartasNoUsadas.removeAll(cartasUsadas);
            return cartasNoUsadas;
        }

        // Si no encontramos ningún trio ni escalera de ≥3 cartas
        return new ArrayList<>(mano);
    }
    // Dentro de la clase ComprobarMano (juego.servicios)

    // Las cartas 10,11, y 12 valen 10, no su valor
    public static int obtenerValorPuntaje(Carta carta) {
        if (carta.getPalo() == Palo.COMODIN) {
            return 25; // O el valor de penalización que uses (ej: 25, 50)
        }

        int numero = carta.getNumero();

        // Las figuras (10, 11, 12) valen 10 puntos, no su número.
        if (numero >= 10 && numero <= 12) {
            return 10;
        }
        return numero; // As, 2, 3... 7 valen su número
    }
    public static int sumarCartasNoUsadas(List<Carta> mano) {
        List<Carta> noUsadas = cartasNoUsadas(mano);
        //return noUsadas.stream().mapToInt(Carta::getNumero).sum();
        return noUsadas.stream()
                .mapToInt(ComprobarMano::obtenerValorPuntaje)
                .sum();
    }



}
