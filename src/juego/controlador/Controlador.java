package juego.controlador;

import java.beans.PropertyChangeListener;
import juego.modelo.Juego;
import juego.modelo.Jugador;
import juego.modelo.EventoConPayload;
import juego.enumerados.Evento;
import java.beans.PropertyChangeEvent;
import java.util.Scanner;

public class Controlador implements PropertyChangeListener {
    private final Juego modeloJuego;
    private final Scanner scanner;

    // Almacenamos el ID del jugador actual para saber a quién pedir input
    private int idJugadorActual;

    public Controlador(Juego modelo) {
        this.modeloJuego = modelo;
        this.modeloJuego.agregarObservador(this); // Se registra al Modelo
        this.scanner = new Scanner(System.in);
    }

    // --- 1. PROCESAMIENTO DE EVENTOS DEL MODELO ---

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String nombreEvento = evt.getPropertyName();
        Object payload = evt.getNewValue();

        // 1. Convertir el nombre del evento a Enum (para el switch)
        Evento evento = Evento.valueOf(nombreEvento);

        switch (evento) {
            case NUEVO_TURNO:
                this.idJugadorActual = (int) payload; // El payload es el ID del jugador
                Jugador jugador = this.modeloJuego.getJugador(this.idJugadorActual);
                this.mostrarEstadoTurno(jugador);
                this.pedirAccionInicial(jugador);
                break;
            case DESCARTAR_O_CERRAR:
                this.pedirAccionDescarte();
                break;
            case RONDA_TERMINADA:
                this.mostrarPuntajes();
                break;
            case GANASTE:
                this.mostrarGanadorFinal((int)payload);
                break;
            case PERDISTE:
                // Lógica de eliminación...
                break;
            default:
                break;
        }
    }

    // --- 2. MÉTODOS DE VISTA/ENTRADA (Consola) ---

    private void mostrarEstadoTurno(Jugador jugador) {
        System.out.println("\n--- TURNO DE " + jugador.getNombre().toUpperCase() + " (ID: " + jugador.getId() + ") ---");
        System.out.println("TOPE DESCARTE: " + this.modeloJuego.getTopePila().toString());
        System.out.println("TU MANO:\n" + jugador.getMano().toString());
        System.out.println("PUNTOS TOTALES: " + jugador.getPuntos());
    }

    private void pedirAccionInicial(Jugador jugador) {
        System.out.println("\n¿Qué deseas hacer? (1: Tomar del Mazo / 2: Tomar de la Pila)");
        int opcion = this.leerOpcion();

        try {
            if (opcion == 1) {
                this.modeloJuego.tomarTopeMazo(jugador.getId());
            } else if (opcion == 2) {
                this.modeloJuego.tomarTopePilaDescarte(jugador.getId());
            }
        } catch (Exception e) {
            System.out.println("Opción inválida o error. Se pasa el turno.");
            // Manejo de error más robusto sería necesario
        }
    }

    private void pedirAccionDescarte() {
        Jugador actual = this.modeloJuego.getJugador(this.idJugadorActual);
        System.out.println("\n[ACCIÓN REQUERIDA] (1-" + actual.getMano().size() + ": Descartar Carta / 0: Cerrar)");

        int opcion = this.leerOpcion();

        try {
            if (opcion == 0) {
                // Intenta cerrar la ronda
                this.modeloJuego.terminarRonda(actual.getId());
            } else if (opcion >= 1 && opcion <= actual.getMano().size()) {
                // Descartar una carta por su índice
                this.modeloJuego.descartar(opcion, actual.getId());
            } else {
                System.out.println("Acción no reconocida. Intenta de nuevo.");
            }
        } catch (Exception e) {
            System.out.println("ERROR: No se pudo realizar la acción. El juego podría estar en un estado inválido.");
        }
    }

    // --- MÉTODOS DE FIN Y UTILIDAD ---

    private void mostrarGanadorFinal(int idGanador) {
        Jugador ganador = this.modeloJuego.getJugador(idGanador);
        System.out.println("\n==================================");
        System.out.println("!!! PARTIDA TERMINADA. GANADOR: " + ganador.getNombre().toUpperCase() + " !!!");
        System.out.println("==================================");
    }

    private void mostrarPuntajes() {
        // Muestra puntajes al final de la ronda
        System.out.println("\n--- RESULTADO DE RONDA ---");
        for (Jugador j : this.modeloJuego.getJugadores()) {
            System.out.println(j.getNombre() + ": " + j.getPuntos() + " pts.");
        }
    }

    // Método seguro para leer enteros de la consola
    private int leerOpcion() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.next(); // Limpia el buffer
            return -1;
        }
    }

    // --- MÉTODOS PARA EL LANZADOR (Main) ---

    public void iniciarJuego() {
        this.modeloJuego.empezarAJugar();
    }

    public void agregarJugador(String nombre) {
        this.modeloJuego.conectarJugador(nombre);
    }
}
