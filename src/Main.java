// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import juego.modelo.Juego;
import juego.controlador.Controlador;
public class Main {
    public static void main(String[] args) {
        System.out.println("\n**** CHINCHON (consola) ****\n");

        // 1. Inicializar el Modelo
        Juego modelo = new Juego();

        // 2. Inicializar el Controlador y registrarlo como Observador
        Controlador controlador = new Controlador(modelo);

        // Delegamos toda la secuencia interactiva al Controlador
        controlador.configurarPartida();


        // El resto de la interacción ocurre dentro del Controlador
    }
}