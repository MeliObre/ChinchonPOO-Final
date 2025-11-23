// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import juego.modelo.Juego;
import juego.controlador.Controlador;
public class Main {
    public static void main(String[] args) {
        System.out.println("\n===== CHINCHON (consola) =====\n");

        // 1. Inicializar el Modelo
        Juego modelo = new Juego();

        // 2. Inicializar el Controlador y registrarlo como Observador
        Controlador controlador = new Controlador(modelo);

        // Delegamos toda la secuencia interactiva al Controlador
        controlador.configurarPartida(); // <-- NUEVO MÉTODO
        //controlador.agregarJugador("Melina"); // Obtiene ID 1
        //controlador.agregarJugador("Profesor IA"); // Obtiene ID 2

        // CORRECCIÓN: Llamar al nuevo método delegado en el Controlador
        //controlador.setListoParaJugar(1, true); // Melina ID 1
        //controlador.setListoParaJugar(2, true); // Profesor IA ID 2
        //System.out.println("Juego configurado con 2 jugadores. Iniciando...");

        // 4. Iniciar el juego (dispara el primer evento)
        //controlador.iniciarJuego();

        // El resto de la interacción ocurre dentro del Controlador
    }
}