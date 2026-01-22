package juego.controlador;



import ar.edu.unlu.rmimvc.cliente.IControladorRemoto;
import ar.edu.unlu.rmimvc.observer.IObservableRemoto;
import juego.interactuar.IJuego;
import juego.modelo.Juego;
import juego.modelo.Jugador;
import juego.modelo.EventoConPayload;
import juego.enumerados.Evento;
import java.beans.PropertyChangeEvent;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

import java.util.InputMismatchException;

public class Controlador implements IControladorRemoto {
    //private final Juego modeloJuego;
    private final Scanner scanner;

    // almaceno el id del jugador actual para saber a quien le tengo qu pedir input
    private int idJugadorActual;
    private IJuego modeloJuego;

    public Controlador() {
        this.scanner = new Scanner(System.in);
    }

    // OBLIGATORIO: Método para que la librería nos de acceso al modelo remoto
    @Override
    public <T extends IObservableRemoto> void setModeloRemoto(T modeloRemoto) {
        this.modeloJuego = (IJuego) modeloRemoto; //
    }
    public void configurarPartida() throws RemoteException {
        System.out.println("--- Configuracion inicial de la partida ---");

        int cantidad = 0;

        // pregunto por la cnaridad de jugadore
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

            int idJugador = this.modeloJuego.conectarJugador(nombre);
            idsConectados.add(idJugador);
        }
        // inicia el juego
        System.out.println("\nJuego configurado con " + cantidad + " jugadores. Iniciando...");

        // todos los jugadores tiene que estar listos

        for (int id = 1; id <= cantidad; id++) {
            this.modeloJuego.setListoParaJugar(id, true);
        }
    }

    // eventos

    public void setListoParaJugar(int idJugador, boolean estaListo) throws RemoteException {
        // La clase Juego (Modelo) ya tiene el método para manejar esta lógica.
        this.modeloJuego.setListoParaJugar(idJugador, estaListo);
    }

    @Override
    public void actualizar(IObservableRemoto modelo, Object cambio) throws RemoteException {


        // En RMI-MVC, el 'cambio' es el objeto que mandaste en notificarObservadores
        if (cambio instanceof EventoConPayload) {
            EventoConPayload ep = (EventoConPayload) cambio;
            Evento evento = ep.getEvento();

            switch (evento) {
                case RONDA_INICIADA:
                    System.out.println("\n==================================");
                    System.out.println("====== INICIO DE RONDA " + ep.getDatoNumerico() + " ======");
                    System.out.println("==================================");
                    break;

                case NUEVO_TURNO:
                    this.idJugadorActual = ep.getDatoNumerico();
                    Jugador j = this.modeloJuego.getJugador(this.idJugadorActual);
                    this.mostrarEstadoTurno(j);
                    this.pedirAccionInicial(j);
                    break;

                case DESCARTAR_O_CERRAR:
                    Jugador actual = this.modeloJuego.getJugador(this.idJugadorActual);
                    System.out.println("\n--- MANO ACTUALIZADA (8 CARTAS) ---");
                    this.mostrarEstadoTurno(actual);
                    this.pedirAccionDescarte();
                    break;

                case RONDA_TERMINADA:
                    this.mostrarPuntajes();
                    break;

                case GANASTE:
                    this.mostrarGanadorFinal(ep.getDatoNumerico());
                    break;
            }
        }

    }

    // --- 2. MÉTODOS DE VISTA/ENTRADA (Consola) ---

    private void mostrarEstadoTurno(Jugador jugador) throws RemoteException {
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
                System.out.println("ERROR INTERNO: No se pudo tomar la carta. " + e.getMessage());
                accionValida = true; // Salir del bucle para evitar reintento eterno en caso de error serio.
            }
        } while (!accionValida); // Repetir mientras la acción no sea válida
    }

    private void pedirAccionDescarte() throws RemoteException {
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
                System.out.println("ERROR: No se pudo realizar la acción. El juego podría estar en un estado inválido.");
                accionValida = true; // Salir para evitar más errores
            }
        } while (!accionValida);
    }

    // --- MÉTODOS DE FIN Y UTILIDAD ---

    private void mostrarGanadorFinal(int idGanador) throws RemoteException {
        Jugador ganador = this.modeloJuego.getJugador(idGanador);
        System.out.println("\n==================================");
        System.out.println("!!! PARTIDA TERMINADA. GANADOR: " + ganador.getNombre().toUpperCase() + " !!!");
        System.out.println("==================================");
        System.exit(0);
    }

    private void mostrarPuntajes() throws RemoteException {
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



    public void iniciarJuego() throws RemoteException {
        this.modeloJuego.empezarAJugar();
    }

    public void agregarJugador(String nombre) throws RemoteException {
        this.modeloJuego.conectarJugador(nombre);
    }


}
