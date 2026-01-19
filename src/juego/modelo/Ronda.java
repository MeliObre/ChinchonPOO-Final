package juego.modelo;

import java.util.ArrayList;

public class Ronda {
    private Mazo mazo ;
    private PilaCarta pilaDescarte = new PilaCarta();
    private int jugadorActual;
    private ArrayList<Jugador> jugadores;

    public Ronda(int JugadorMano, ArrayList<Jugador> jugadores, boolean incluir8y9){
        this.jugadorActual = JugadorMano;
        this.jugadores = jugadores;
        //ajuste, inicializo el mazo con el parametro de 9 y 9
        this.mazo=new Mazo(incluir8y9);
        this.resetManos();
        this.mazo.barajar(); // Comentar para pruebas
        this.mazo.repartir(this.jugadores, 7, false);
        this.pilaDescarte.anadirCarta(this.mazo.tomarCartaTope());
    }

    private  void resetManos(){
        for (int i = 0; i < this.jugadores.size(); i++) {
            this.jugadores.get(i).resetMano();
        }
    }

    public Jugador getJugadorActual(){
        return this.jugadores.get(jugadorActual);
    }

    public void siguienteTurno() {
        if (this.jugadorActual < this.jugadores.size() - 1) {
            //si no es el ultimo jugador, incrementa el indice al siguiente jugador de la lista.
            this.jugadorActual = this.jugadorActual + 1;
        } else {
            this.jugadorActual = 0;
        }
    }

    public Carta getTopePila(){
        return this.pilaDescarte.getTope();
    }
    public void tomarTopePilaDescarte(Jugador jugadorQueToma){
        jugadorQueToma.tomarCartaPilaDescarte(this.pilaDescarte);// no puede tomar la carte que descarto recien
    }

    public void tomarTopeMazo (Jugador jugadorQueToma){
        jugadorQueToma.tomarCartaMazo(this.mazo);
    }

    public void descartar (int cartaElegida, Jugador jugadorQueDescarta){
        jugadorQueDescarta.descartarCarta(cartaElegida, this.pilaDescarte);
    }

    public void sumarPuntos(){
        for (int i = 0; i < this.jugadores.size(); i++){
            jugadores.get(i).añadirPuntosMano();
        }
    }

}
