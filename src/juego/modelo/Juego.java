package juego.modelo;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class Juego {
    private HashMap<Integer, Jugador> jugadores = new HashMap<>();
    private Ronda ronda;
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
    public Jugador[] getJugadores() {
        return jugadores.values().toArray(new Jugador[0]);
    }
    public void setListoParaJugar(int jugador, boolean estaListo) throws RemoteException {
        Jugador jugadorListo = this.getJugador(jugador);
        jugadorListo.setListoParaJugar(true);
        this.empezarAJugar();
    }


    private void agregarJugador(Jugador jugador) {
        this.jugadores.put(jugador.getId(), jugador);
    }

    public void setListoPJugar (int jugador, boolean estaListo) throws RemoteException{
        Jugador jugadorListo = this.getJugadores(jugador);
        jugadorListo.setListoParaJugar(true);
        this.empezarAJugar();
    }

    public void tomarTopeMazo(int jugadorQueToma) throws RemoteException {
        Jugador jugador = this.getJugador(jugadorQueToma);
        if (jugador != this.getJugadorActual())
            return;

        this.ronda.tomarTopeMazo(jugador);
        this.notificarObservadores(Evento.DESCARTAR_O_CERRAR);
    }
    public void tomarTopePilaDescarte(int jugadorQueToma) throws RemoteException {
        Jugador jugador = this.getJugador(jugadorQueToma);
        if (jugador != this.getJugadorActual())
            return;
        this.ronda.tomarTopePilaDescarte(jugador);
        this.notificarObservadores(Evento.DESCARTAR_O_CERRAR);
    }

    public void descartar(int cartaElegida, int jugadorQueDescarta) throws RemoteException {
        Jugador jugador = this.getJugador(jugadorQueDescarta);
        if (jugador != this.getJugadorActual())
            return;

        this.ronda.descartar(cartaElegida, jugador);
        this.siguienteTurno();
    }

    public Mano getMano(int numJugador) {
        return this.getJugador(numJugador).getMano();
    }

    public void nuevaRonda() throws RemoteException {
        ArrayList<Jugador> listaJugadores = new ArrayList<>(jugadores.values());
        int numeroJugadorMano = (int) (Math.random() * (jugadores.size() - 1));
        this.ronda = new Ronda(numeroJugadorMano, listaJugadores);
        this.notificarObservadores(Evento.NUEVO_TURNO);
    }

    public void siguienteTurno() throws RemoteException {
        this.ronda.siguienteTurno();
        this.notificarObservadores(Evento.NUEVO_TURNO);
    }

    public Jugador getJugadorActual() {
        return this.ronda.getJugadorActual();
    }

    public Carta getTopePila() {
        return this.ronda.getTopePila();
    }


    public void empezarAJugar() throws RemoteException {
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

    private void eliminarPerdedor(Jugador perdedor) throws RemoteException {
        EventoConPayload eventoPerder = new EventoConPayload(Evento.PERDISTE, perdedor.getId());
        this.notificarObservadores(eventoPerder);
        this.jugadores.remove(perdedor.getId());
    }

    private void declararGanador(Jugador ganador) throws RemoteException {
        EventoConPayload eventoGanar = new EventoConPayload(Evento.GANASTE, ganador.getId());
        this.notificarObservadores(eventoGanar);
        // guardar jugador en el top de ganadores
    }

    public void terminarRonda(int idJugadorQueCierra) throws RemoteException {
        Jugador jugadorQueCierra = this.getJugador(idJugadorQueCierra);
        TopJugadores.getInstancia().agregarJugador(jugadorQueCierra);
        if (!jugadorQueCierra.getMano().esCerrable())
            return;

        if (jugadorQueCierra != this.getJugadorActual())
            return;

        int puntajeDeCierre = jugadorQueCierra.getMano().cerrarMano();
        if (puntajeDeCierre <= -100) {
            // victoria total y absoluta por chinchon
            declararGanador(jugadorQueCierra);
            return;
        }

        this.ronda.sumarPuntos();
        this.notificarObservadores(Evento.RONDA_TERMINADA);
        for (Jugador jugador : this.jugadores.values()) {
            if (jugador.getPuntos() >= 100) {
                eliminarPerdedor(jugador);
            }
            jugador.setListoParaJugar(false);
        }
        if (this.getCantidadJugadores() < 2) {
            declararGanador(this.getJugadores()[0]);
        }
    }

    public int getCantidadJugadores() {
        return this.jugadores.size();
    }

    public Jugador getJugador(int idJugador) {
        return this.jugadores.get(idJugador);
    }

    public void testearConectividad() throws RemoteException {
        System.out.println("mostrando mensaje en el servidor");
    }

    public String getJugadoresTopString() throws RemoteException {
        return TopJugadores.getInstancia().getJugadoresTopString();
    }
}
