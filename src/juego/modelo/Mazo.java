package juego.modelo;

import java.util.ArrayList;
import java.util.Collections;

public class Mazo extends PilaCarta{
    public Mazo(boolean incluirOchoYnueve){
        // Crear las 48 cartas normales (1-12 por palo)
        // itero sobre todos los valores del enum Palo
        for (Palo palo : Palo.values()){
            if (palo != Palo.COMODIN){
                for (int i = 1; i <= 12 ; i++){
                    //Si no quiero 8 y 9, salteo
                    if (!incluirOchoYnueve && (i== 8 || i == 9)){
                        continue;
                    }
                    Carta cartaAñadir = new Carta(palo, i);
                    this.anadirCarta(cartaAñadir);
                }
            }
        }

        // COMODINES

        this.anadirCarta(new Carta(Palo.COMODIN, 0));
        this.anadirCarta(new Carta(Palo.COMODIN, 0));
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
