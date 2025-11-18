// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import juego.modelo.Juego;
import juego.controlador.Controlador;
public class Main {
    public static void main(String[] args) {
        System.out.println("\n===== CHINCHÓN POO CONSOLA =====\n");

        // 1. Inicializar el Modelo
        Juego modelo = new Juego();

        // 2. Inicializar el Controlador y registrarlo como Observador
        Controlador controlador = new Controlador(modelo);

        // 3. Configurar el inicio del juego
        controlador.agregarJugador("Melina");
        controlador.agregarJugador("Profesor IA");

        System.out.println("Juego configurado con 2 jugadores. Iniciando...");

        // 4. Iniciar el juego (dispara el primer evento)
        controlador.iniciarJuego();

        // El resto de la interacción ocurre dentro del Controlador
    }
}