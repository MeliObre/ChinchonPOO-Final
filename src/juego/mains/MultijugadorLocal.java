package juego.mains;

import juego.controlador.Controlador;
import juego.modelo.Juego;
import juego.vista.IVista;
import juego.vista.pseudoconsola.*;

import java.rmi.RemoteException;

public class MultijugadorLocal {
    public static void main(String[] args) throws RemoteException {
        // 1. Un solo modelo en memoria para ambos
        Juego modelo = new Juego();

        // 2. Creamos la primera vista (Consola del Jugador 1)
        Controlador controlador1 = new Controlador();
        controlador1.setModeloRemoto(modelo); // Vinculación local
        IVista vista1 = new Pseudoconsola(controlador1);
        modelo.agregarObservador(controlador1);

        // 3. Creamos la segunda vista (Consola del Jugador 2)
        Controlador controlador2 = new Controlador();
        controlador2.setModeloRemoto(modelo); // Vinculación local
        IVista vista2 = new Pseudoconsola(controlador2);
        modelo.agregarObservador(controlador2);

        // 4. Iniciamos las interfaces (esto abrirá la VentanaInicioSesion)
        vista1.iniciar();
        vista2.iniciar();
    }
}
