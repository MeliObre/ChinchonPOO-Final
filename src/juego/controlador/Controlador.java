package juego.controlador;

import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import juego.interactuar.IJuego;
import juego.modelo.Carta;
import juego.modelo.EventoConPayload;
import juego.modelo.Jugador;
import juego.enumerados.Evento;
import juego.vista.IVista;

import java.rmi.RemoteException;


public class Controlador implements IControladorRemoto {
    //private final Juego modeloJuego;

    private IVista vista;
    private int idJugador;
    private int idJugadorActual;
    private IJuego modeloJuego;

    public Controlador() {
    }
    public void setVista(IVista vista) {
        this.vista = vista;
    }

    // OBLIGATORIO: Método para que la librería nos de acceso al modelo remoto
    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) {
        this.modeloJuego = (IJuego) modeloRemoto; //
    }


    public void setListoParaJugar(boolean listo) throws RemoteException {
        // Usamos el idJugador que guardamos al conectarnos
        this.modeloJuego.setListoParaJugar(this.idJugador, listo);
    }

    @Override
    public void actualizar(IObservableRemoto modelo, Object cambio) throws RemoteException {
        if (cambio instanceof EventoConPayload) {
            EventoConPayload ep = (EventoConPayload) cambio;
            Evento evento = ep.getEvento();

            switch (evento) {
                case NUEVO_TURNO:
                    this.idJugadorActual = ep.getDatoNumerico();
                    if (this.idJugador == this.idJugadorActual) {
                        vista.actualizarManoYPila();
                        vista.tomarDeMazoOPila();
                    } else {
                        vista.bloquear();
                    }
                    break;

                case DESCARTAR_O_CERRAR:
                    if (this.idJugador == this.idJugadorActual) {
                        vista.actualizarManoYPila();
                        vista.descartarOCerrar();
                    }
                    break;

                case RONDA_TERMINADA:
                    vista.mostrarPuntos();
                    vista.esperarNuevaRonda();
                    break;

                case GANASTE:
                    if (ep.getDatoNumerico() == this.idJugador) {
                        vista.ganar();
                    }
                    break;

                case PERDISTE:
                    if (ep.getDatoNumerico() == this.idJugador) {
                        vista.perder();
                    }
                    break;
            }
        }
    }

    public void conectarJugador(String nombre) {
        try {
            // Guardamos el ID que nos asigna el modelo al entrar
            this.idJugador = this.modeloJuego.conectarJugador(nombre);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void tomarTopeMazo() throws RemoteException {
        this.modeloJuego.tomarTopeMazo(this.idJugador);
    }

   public void tomarTopePilaDescarte() throws RemoteException {
       this.modeloJuego.tomarTopePilaDescarte(this.idJugador);
   }
   public void descartar(int indice) throws RemoteException {
      this.modeloJuego.descartar(indice, this.idJugador);
    }

    public Jugador getJugadorActual() throws RemoteException {
        return this.modeloJuego.getJugadorActual();
    }

    public Jugador[] getJugadores() throws RemoteException {
        return this.modeloJuego.getJugadores();
    }

    public Jugador getJugador() throws RemoteException {
        return this.modeloJuego.getJugador(this.idJugador);
    }

    public Jugador getJugador(int id) throws RemoteException {
        return this.modeloJuego.getJugador(id);
    }

    public int getCantidadJugadores() throws RemoteException {
        return this.modeloJuego.getCantidadJugadores();
    }

    public void terminarRonda() throws RemoteException {
        this.modeloJuego.terminarRonda(this.idJugador);
    }

    public Carta getTopePila() throws RemoteException {
        return this.modeloJuego.getTopePila();
    }

    public void testConectado() throws RemoteException {
        this.modeloJuego.testearConectividad();
    }

    public String getJugadoresTopString() throws RemoteException {
        return this.modeloJuego.getJugadoresTopString();
    }

}
