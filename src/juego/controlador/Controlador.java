package juego.controlador;

import java.beans.PropertyChangeListener;
import juego.modelo.Juego;
import juego.modelo.Jugador;
import juego.modelo.EventoConPayload;
import juego.enumerados.Evento;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.InputMismatchException;

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

    public void configurarPartida() {
        System.out.println("--- Configuracion inicial de la partida ---");

        int cantidad = 0;

        // 1. Preguntar por la cantidad de jugadores (Bucle do-while)
        do {
            System.out.println("¿Cuántos jugadores participarán? (2, 3 o 4)");
            cantidad = this.leerOpcion();

            if (cantidad < 2 || cantidad > 4) {
                System.out.println("Cantidad inválida. El juego requiere entre 2 y 4 jugadores. Intenta de nuevo.");
            }
        } while (cantidad < 2 || cantidad > 4);

        scanner.nextLine();

        ArrayList<Integer> idsConectados = new ArrayList<>();

        for (int i = 1; i <= cantidad; i++) {
            System.out.print("Ingrese el nombre del Jugador " + i + ": ");

            String nombre = scanner.nextLine();

            // El resto del código de conexión
            int idJugador = this.modeloJuego.conectarJugador(nombre);
            idsConectados.add(idJugador);
        }
        // Iniciar el juego
        System.out.println("\nJuego configurado con " + cantidad + " jugadores. Iniciando...");

        //Todos los jugadores deben si o si estar LISTOS antes de iniciar con la partida

        for (int id = 1; id <= cantidad; id++) {
            this.modeloJuego.setListoParaJugar(id, true);
        }
    }

    // --- 1. PROCESAMIENTO DE EVENTOS DEL MODELO ---

    public void setListoParaJugar(int idJugador, boolean estaListo) {
        // La clase Juego (Modelo) ya tiene el método para manejar esta lógica.
        this.modeloJuego.setListoParaJugar(idJugador, estaListo);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String nombreEvento = evt.getPropertyName();
        Object payload = evt.getNewValue();

        // 1. MANEJO DE EVENTOS PERSONALIZADOS (Fuera del Enum Evento)
        if (nombreEvento.equals("RONDA_INICIADA")) {
            int numRonda = (int) payload;
            System.out.println("\n==================================");
            System.out.println("====== INICIO DE RONDA " + numRonda + " ======");
            System.out.println("==================================");
            return; // Termina la función aquí, ya que el evento NUEVO_TURNO vendrá después
        }

        // 2. CONVERTIR EL NOMBRE DEL EVENTO A ENUM (Lógica existente)
        // Solo llegamos aquí si el evento es uno de los valores del enum (NUEVO_TURNO, RONDA_TERMINADA, etc.)
        Evento evento = Evento.valueOf(nombreEvento);

        switch (evento) {
            case NUEVO_TURNO:
                this.idJugadorActual = (int) payload;
                Jugador jugador = this.modeloJuego.getJugador(this.idJugadorActual);
                this.mostrarEstadoTurno(jugador);
                this.pedirAccionInicial(jugador);
                break;

            case DESCARTAR_O_CERRAR:
                this.pedirAccionDescarte();
                break;

            case RONDA_TERMINADA:
                // Llama a la nueva función de visualización al final de la ronda
                this.mostrarPuntajes();
                // NO se necesita break o return aquí si no hay más lógica.
                break;

            case GANASTE:
                // CORRECCIÓN CLAVE: El payload es un objeto EventoConPayload
                // que contiene el ID del ganador en su dato numérico.
                if (payload instanceof EventoConPayload) {
                    EventoConPayload eventoGanaste = (EventoConPayload) payload;

                    // Obtenemos el ID del ganador desde el payload
                    int idGanador = eventoGanaste.getDatoNumerico();

                    // Llamamos a la función de visualización
                    this.mostrarGanadorFinal(idGanador);
                }
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
        boolean accionValida = false;
        do {
            System.out.println("¿Qué deseas hacer? (1: Tomar del Mazo / 2: Tomar de la Pila)");
            int opcion = this.leerOpcion();

            try {
                if (opcion == 1) {
                    this.modeloJuego.tomarTopeMazo(jugador.getId());
                    accionValida = true; // La acción fue exitosa
                } else if (opcion == 2) {
                    this.modeloJuego.tomarTopePilaDescarte(jugador.getId());
                    accionValida = true; // La acción fue exitosa
                } else {
                    System.out.println("Opción incorrecta. Por favor, ingresa 1 o 2.");
                    // accionValida sigue siendo false, el bucle se repite
                }
            } catch (Exception e) {
                // Este catch es para errores más serios del Modelo (ej: mazo vacío)
                System.out.println("⚠️ ERROR INTERNO: No se pudo tomar la carta. " + e.getMessage());
                accionValida = true; // Salir del bucle para evitar reintento eterno en caso de error serio.
            }
        } while (!accionValida); // Repetir mientras la acción no sea válida
    }

    private void pedirAccionDescarte() {
        Jugador actual = this.modeloJuego.getJugador(this.idJugadorActual);
        boolean accionValida = false;

        do {
            System.out.println("\n[ACCIÓN REQUERIDA] (1-" + actual.getMano().getCantidadCartas() + ": Descartar Carta / 0: Cerrar)");
            int opcion = this.leerOpcion();

            try {
                if (opcion == 0) {
                    this.modeloJuego.terminarRonda(actual.getId());
                    accionValida = true;
                } else if (opcion >= 1 && opcion <= actual.getMano().getCantidadCartas()) {
                    this.modeloJuego.descartar(opcion, actual.getId());
                    accionValida = true;
                } else {
                    System.out.println("Índice o acción incorrecta. Por favor, intenta de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("⚠️ ERROR: No se pudo realizar la acción. El juego podría estar en un estado inválido.");
                accionValida = true; // Salir para evitar más errores
            }
        } while (!accionValida);
    }

    // --- MÉTODOS DE FIN Y UTILIDAD ---

    private void mostrarGanadorFinal(int idGanador) {
        Jugador ganador = this.modeloJuego.getJugador(idGanador);
        System.out.println("\n==================================");
        System.out.println("!!! PARTIDA TERMINADA. GANADOR: " + ganador.getNombre().toUpperCase() + " !!!");
        System.out.println("==================================");
        System.exit(0);
    }

    private void mostrarPuntajes() {
        System.out.println("--- PUNTAJES ACUMULADOS ---");

        // Obtenemos los jugadores directamente del Modelo para ver sus puntos actuales
        for (Jugador jugador : this.modeloJuego.getJugadores()) {
            System.out.println(" - " + jugador.getNombre() + ": " + jugador.getPuntos() + " puntos.");
        }
        System.out.println("---------------------------\n");
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
