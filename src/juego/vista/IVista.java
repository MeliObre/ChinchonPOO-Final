package juego.vista;
import java.rmi.RemoteException;

public interface IVista {
    void iniciar(); // Para arrancar la interfaz (consola o frame)
    void bloquear() throws RemoteException; // Para deshabilitar input cuando no es tu turno
    void actualizarManoYPila() throws RemoteException; // Refrescar visualmente las cartas
    void tomarDeMazoOPila() throws RemoteException; // Pedir al usuario que robe una carta
    void descartarOCerrar() throws RemoteException; // Pedir al usuario que tire o corte
    void mostrarPuntos() throws RemoteException; // Mostrar tabla de puntajes
    void perder() throws RemoteException; // Pantalla de derrota
    void ganar() throws RemoteException; // Pantalla de victoria
    void esperarNuevaRonda() throws RemoteException; // Estado de espera entre rondas

}
