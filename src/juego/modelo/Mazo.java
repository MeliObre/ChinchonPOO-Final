package juego.modelo;

import java.util.ArrayList;
import java.util.Collections;

public class Mazo extends PilaCarta{
    public Mazo(){
        for (int i = 1; i <= 12 ; i++){
            Carta cartaAñadir = new Carta(Palo.BASTO, i);
            this.anadirCarta(cartaAñadir);
        }
        for (int i = 1; i <= 12 ; i++){
            Carta cartaAñadir = new Carta(Palo.ESPADA, i);
            this.anadirCarta(cartaAñadir);
        }
        for (int i = 1; i <= 12 ; i++){
            Carta cartaAñadir = new Carta(Palo.ORO, i);
            this.anadirCarta(cartaAñadir);
        }
        for (int i = 1; i <= 12 ; i++){
            Carta cartaAñadir = new Carta(Palo.COPA, i);
            this.anadirCarta(cartaAñadir);
        }
    }

    public void barajar(){
        Collections.shuffle(this.getCartas());
    }

    public void repartir (ArrayList<Jugador> jugadoresIn , int cantidadCartas, boolean darCartaExtra){
        for (int i = 0; i < jugadoresIn.size(); i++){
            for (int c = 0; c < 7; c++ ){
                jugadoresIn.get(i).tomarCartaMazo(this);
            }
        }
        if (darCartaExtra){
            jugadoresIn.get(0).tomarCartaMazo(this);
        }
    }
    public void reutilizarPilaDescarte(){

    }
}
