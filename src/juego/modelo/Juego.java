package juego.modelo;

import ar.edu.unlu.rmimvc.observer.ObservableRemoto;
import juego.interactuar.IJuego;
import juego.servicios.TopJugadores;
import juego.enumerados.Evento;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class Juego  implements Serializable {
    private HashMap<Integer, Jugador> jugadores = new HashMap<>();
    private Ronda ronda;

    private int contadorRonda = 0;

    private boolean incluir8y9 = true;
    public int conectarJugador(String nombre) {
        Jugador jugador = new Jugador(nombre);
        agregarJugador(jugador);
        System.out.println("Se conectó el jugador: " + jugador.getNombre() + " id:" + jugador.getId());
        return jugador.getId();
    }
    public void desconectarJugador(int usuarioId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'desconectarJugador'");
    }
    private transient PropertyChangeSupport cambios = new PropertyChangeSupport(this);

    // metodos del Observer
    public void agregarObservador(PropertyChangeListener listener) {
        cambios.addPropertyChangeListener(listener);
    }
    public void removerObservador(PropertyChangeListener listener) {
        cambios.removePropertyChangeListener(listener);
    }
    //------------------------------------------------
    public Jugador[] getJugadores() {

        return jugadores.values().toArray(new Jugador[0]);
    }
    public void setListoParaJugar(int idJugador, boolean estaListo)  {
        Jugador jugadorListo = this.getJugador(idJugador);

        // usa el valor del parámetro (true o false)
        jugadorListo.setListoParaJugar(estaListo);

        // Solo intenta empezar si el jugador se puso en 'true'
        if (estaListo) {
            this.empezarAJugar();
        }
    }


    private void agregarJugador(Jugador jugador) {
        this.jugadores.put(jugador.getId(), jugador);
    }

    public void tomarTopeMazo(int jugadorQueToma)  {
        Jugador jugador = this.getJugador(jugadorQueToma);
        if (jugador != this.getJugadorActual())
            return;

        this.ronda.tomarTopeMazo(jugador);
        //this.notificarObservadores(Evento.DESCARTAR_O_CERRAR);
         this.cambios.firePropertyChange(Evento.DESCARTAR_O_CERRAR.toString(), null, null);
    }
    public void tomarTopePilaDescarte(int jugadorQueToma)  {
        Jugador jugador = this.getJugador(jugadorQueToma);
        if (jugador != this.getJugadorActual())
            return;
        this.ronda.tomarTopePilaDescarte(jugador);
        //this.notificarObservadores(Evento.DESCARTAR_O_CERRAR);
        this.cambios.firePropertyChange(Evento.DESCARTAR_O_CERRAR.toString(), null, null);
    }

    public void descartar(int cartaElegida, int jugadorQueDescarta)  {
        Jugador jugador = this.getJugador(jugadorQueDescarta);
        if (jugador != this.getJugadorActual())
            return;

        this.ronda.descartar(cartaElegida, jugador);
        this.siguienteTurno();
    }

    public Mano getMano(int numJugador) {
        return this.getJugador(numJugador).getMano();
    }

    public void nuevaRonda() {
        this.contadorRonda++; //  INCREMENTA EL CONTADOR

        ArrayList<Jugador> listaJugadores = new ArrayList<>(jugadores.values());
        int numeroJugadorMano = (int) (Math.random() * (jugadores.size() - 1));
        this.ronda = new Ronda(numeroJugadorMano, listaJugadores, this.incluir8y9);

        //  NOTIFICA LA INICIACION DE LA RONDA con el numero como payload
        // PARA LA VISTA CONSOLA INICIALMNTE
        this.cambios.firePropertyChange("RONDA_INICIADA", null, this.contadorRonda);

        // Notificacion del primer turno
        this.cambios.firePropertyChange(Evento.NUEVO_TURNO.toString(), null, this.ronda.getJugadorActual().getId());
    }

    public void siguienteTurno() {
        this.ronda.siguienteTurno();
        //this.notificarObservadores(Evento.NUEVO_TURNO);
        this.cambios.firePropertyChange(Evento.NUEVO_TURNO.toString(), null, this.ronda.getJugadorActual().getId());
    }

    public Jugador getJugadorActual() {
        return this.ronda.getJugadorActual();
    }

    public Carta getTopePila() {
        return this.ronda.getTopePila();
    }


    public void empezarAJugar()  {
        if (this.jugadores.size() < 2) {
            System.out.println("no hay suficientes jugadores para empezar");
            return;
        }

        for (Jugador jugador : this.jugadores.values()) {
            if (!jugador.getListoParaJugar()) {
                System.out.println("el jugador [" + jugador.getNombre() + "] aún no está listo");
                return;
            }
        }

        this.nuevaRonda();
    }

    private void eliminarPerdedor(Jugador perdedor) {
        EventoConPayload eventoPerder = new EventoConPayload(Evento.PERDISTE, perdedor.getId());
        this.cambios.firePropertyChange(Evento.PERDISTE.toString(), null, eventoPerder);
        //this.notificarObservadores(eventoPerder);
        this.jugadores.remove(perdedor.getId());
    }

    private void declararGanador(Jugador ganador)  {
        EventoConPayload eventoGanar = new EventoConPayload(Evento.GANASTE, ganador.getId());
        //this.notificarObservadores(eventoGanar);
        this.cambios.firePropertyChange(Evento.GANASTE.toString(), null, eventoGanar);
        // guardar jugador en el top de ganadores
    }

    public void terminarRonda(int idJugadorQueCierra)  {
        Jugador jugadorQueCierra = this.getJugador(idJugadorQueCierra);

        // Logica de Chequeo y Puntuacion
        TopJugadores.getInstancia().agregarJugador(jugadorQueCierra); // Registra la victoria de la ronda
        if (!jugadorQueCierra.getMano().esCerrable())
            return; // No puede cerrar
        if (jugadorQueCierra != this.getJugadorActual())
            return; // No es su turno

        // determinar puntuacion y victoria absoluta
        int puntajeDeCierre = jugadorQueCierra.getMano().cerrarMano();
        if (puntajeDeCierre <= -100) {
            // Victoria total por chinchon (el juego termina inmediatamente)
            declararGanador(jugadorQueCierra);
            return;
        }

        // sumar Puntos y notificar Fin de Ronda
        this.ronda.sumarPuntos();
        this.cambios.firePropertyChange(Evento.RONDA_TERMINADA.toString(), null, null);

        // Chequear y Eliminar Perdedores (si superan el limite de 100)
        // Usamos una lista temporal para evitar modificar el mapa 'jugadores' mientras iteramos
        ArrayList<Jugador> jugadoresEliminados = new ArrayList<>();
        for (Jugador jugador : this.jugadores.values()) {
            if (jugador.getPuntos() >= 100) {
                jugadoresEliminados.add(jugador);
            }
            // Reiniciar estado para la nueva ronda
            jugador.setListoParaJugar(false);
        }

        // Ejecutar la eliminacion
        for (Jugador perdedor : jugadoresEliminados) {
            eliminarPerdedor(perdedor); // Este metodo elimina del mapa this.jugadores
        }

        // logica de Continuación/Fin de Partida (El Bucle de Partida)

        if (this.getCantidadJugadores() < 2) {
            // Si queda 1 jugador o menos, el juego termina y el ultimo jugador gana.
            if (this.getCantidadJugadores() == 1) {
                declararGanador(this.getJugadores()[0]);
            }
            return;
        }

        // CORRECCION CLAVE: Si quedan 2 o mAs jugadores, se inicia la siguente ronda
        this.nuevaRonda();
    }

    public int getCantidadJugadores() {
        return this.jugadores.size();
    }

    public Jugador getJugador(int idJugador) {
        return this.jugadores.get(idJugador);
    }

    public void testearConectividad()  {
        System.out.println("mostrando mensaje en el servidor");
    }

    public String getJugadoresTopString()  {
        return TopJugadores.getInstancia().getJugadoresTopString();
    }
}
