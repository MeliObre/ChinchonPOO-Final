package juego.mains;

import juego.controlador.Controlador;
import juego.modelo.Juego;

import java.rmi.RemoteException;

public class MultijugadorLocal {
    public static void main(String[] args) throws RemoteException {
        // Modelo único en memoria
        Juego modelo = new Juego();

        // Jugador 1 - Consola
        Controlador controlador1 = new Controlador();
        controlador1.setModeloRemoto(modelo); // Lo vinculamos localmente
        modelo.agregarObservador(controlador1);

        // Jugador 2 - Consola (u otra vista)
        Controlador controlador2 = new Controlador();
        controlador2.setModeloRemoto(modelo);
        modelo.agregarObservador(controlador2);

        // Iniciamos la partida
        controlador1.configurarPartida();
    }
}
