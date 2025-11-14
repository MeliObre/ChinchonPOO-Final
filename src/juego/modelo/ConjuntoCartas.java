package juego.modelo;

import java.io.Serializable;
import java.util.*;

public class ConjuntoCartas implements Serializable {
    protected ArrayList<Carta> cartas = new ArrayList<Carta>();


    public ConjuntoCartas(){

    }

    public void anadirCarta(Carta carta){
        if (carta != null){
            this.cartas.add(carta);
        }
    }
    protected ArrayList<Carta> getCartas(){
        return this.cartas;
    }
    public int getCantidadCartas (){
        return this.cartas.size();
    }

    public Carta getCarta (int posicionCarta){
        if (posicionCarta < 1){
            return null;
        }
        if (posicionCarta > this.getCantidadCartas()){
            return null;
        }
        return this.cartas.get(posicionCarta - 1);
    }
    public void transferirCarta (int posicionCarta, ConjuntoCartas receptor){
        receptor.anadirCarta(this.cartas.get(posicionCarta - 1));
        this.cartas.remove(posicionCarta - 1);
    }
    public void quitarCarta(Carta cartaAQuitar) {
        this.getCartas().remove(cartaAQuitar);
    }

    public ConjuntoCartas getCartas(int[] posiciones) {
        if ((posiciones.length < 1) || (posiciones.length > this.getCantidadCartas())) {
            return null;
        }

        HashSet<Integer> set = new HashSet<Integer>();
        ConjuntoCartas conjunto = new ConjuntoCartas();

        for (int i = 0; i < posiciones.length; i++) {
            if (set.add(posiciones[i]) == false) {
                return null;
            }
            conjunto.anadirCarta(this.getCarta((posiciones[i])));
        }
        return conjunto;
    }

    public Carta tomarCarta(int posicionCarta){
        Carta devolver = this.cartas.get(posicionCarta - 1);
        this.cartas.remove(posicionCarta - 1);
        return devolver;
    }

    public void ordenarCarta(){
        Collections.sort(this.getCartas(), new Comparator<Carta>() {
            @Override
            public int compare(Carta carta1, Carta carta2) {
                if (carta1.getNumero()==carta2.getNumero())
                    return 0;
                return carta1.getNumero()< carta2.getNumero() ? -1 :1;
            }
        });
    }

    public void añadirConjunto(ConjuntoCartas conjuntoCartasAñadir) {
        for (int i = 1; i <= conjuntoCartasAñadir.getCantidadCartas(); i++) {
            this.anadirCarta(conjuntoCartasAñadir.getCarta(i));
        }
    }
    public boolean quitarConjuntoCartas(ConjuntoCartas cartasAQuitar) {
        HashSet<Carta> cartasQueAparecieron = new HashSet<Carta>();
        ConjuntoCartas conjuntoSalida = new ConjuntoCartas();

        for (int i = 1; i <= cartasAQuitar.getCantidadCartas(); i++) {
            cartasQueAparecieron.add(cartasAQuitar.getCarta(i));
        }
        ;
        for (int i = 1; i <= this.getCantidadCartas(); i++) {
            if (cartasQueAparecieron.add(this.getCarta(i))) {
                conjuntoSalida.anadirCarta(this.getCarta(i));
            }
        }
        this.cartas = conjuntoSalida.cartas;
        return true;
    }
    public ConjuntoCartas devolverCopiaConjunto() {
        ConjuntoCartas conjuntoCopia = new ConjuntoCartas();

        for (int i = 1; i <= this.getCantidadCartas(); i++) {
            conjuntoCopia.anadirCarta(this.getCarta(i));
        }
        return conjuntoCopia;
    }


    public String toString() {
        if (this.getCantidadCartas() == 0) {
            return " NO HAY CARTAS ";
        }
        String result = "";
        for (int i = 1; i <= this.getCantidadCartas(); i++) {
            result = i + ". " + this.getCarta(i).toString() + "\n";
        }
        return result;
    }

    public ArrayList<Carta> getCopiaArrayList() {
        return new ArrayList<Carta>(cartas);
    }

    public static ConjuntoCartas arrayToConjuntoCartas(List<Carta> cartas) {
        ConjuntoCartas resultado = new ConjuntoCartas();
        for (int i = 0; i < cartas.size(); i++) {
            resultado.anadirCarta(cartas.get(i));
        }
        return resultado;
    }

}
