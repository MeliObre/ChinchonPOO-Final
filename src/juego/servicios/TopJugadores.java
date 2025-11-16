package juego.servicios;

import juego.modelo.Jugador;
import juego.modelo.JugadorRanking;

import java.io.*;
import java.util.*;

public class TopJugadores {
    private static TopJugadores instancia;

    private List<JugadorRanking> listaJugadoresTop;
    private final String nombreArchivoTopJugadores = "top_jugadores.ser";
    private final int cantidadMaximaTop = 10;

    private TopJugadores() {
        this.listaJugadoresTop = this.cargarJugadoresDesdeArchivo();
    }

    public static TopJugadores getInstancia() {
        if (instancia == null) {
            instancia = new TopJugadores();
        }
        return instancia;
    }

    public void agregarJugador(Jugador jugadorNuevo) {
        for (JugadorRanking jugadorTop : this.listaJugadoresTop) {
            if (jugadorTop.getNombre().equalsIgnoreCase(jugadorNuevo.getNombre())) {
                jugadorTop.aumentarRondasGanadas();
                this.ordenarYLimitarLista();
                this.guardarJugadoresEnArchivo();
                return;
            }
        }

        JugadorRanking nuevoJugadorTop = new JugadorRanking(jugadorNuevo.getNombre(), 1);
        this.listaJugadoresTop.add(nuevoJugadorTop);
        this.ordenarYLimitarLista();
        this.guardarJugadoresEnArchivo();
    }

    public List<JugadorRanking> getJugadoresTop() {
        return this.listaJugadoresTop;
    }

    private void ordenarYLimitarLista() {
        this.listaJugadoresTop.sort((a, b) -> Integer.compare(b.getRondasGanadas(), a.getRondasGanadas()));
        if (this.listaJugadoresTop.size() > this.cantidadMaximaTop) {
            this.listaJugadoresTop = new ArrayList<>(this.listaJugadoresTop.subList(0, this.cantidadMaximaTop));
        }
    }

    private void guardarJugadoresEnArchivo() {
        try (ObjectOutputStream flujoSalida = new ObjectOutputStream(
                new FileOutputStream(this.nombreArchivoTopJugadores))) {
            flujoSalida.writeObject(this.listaJugadoresTop);
        } catch (IOException excepcion) {
            excepcion.printStackTrace();
        }
    }

    private List<JugadorRanking> cargarJugadoresDesdeArchivo() {
        File archivo = new File(this.nombreArchivoTopJugadores);
        if (!archivo.exists())
            return new ArrayList<>();

        try (ObjectInputStream flujoEntrada = new ObjectInputStream(
                new FileInputStream(this.nombreArchivoTopJugadores))) {
            return (List<JugadorRanking>) flujoEntrada.readObject();
        } catch (IOException | ClassNotFoundException excepcion) {
            excepcion.printStackTrace();
            return new ArrayList<>();
        }
    }

    public String getJugadoresTopString() {
        return formatearJugadoresTop(this.getJugadoresTop());
    }

    private static String formatearJugadoresTop(List<JugadorRanking> jugadoresTop) {
        StringBuilder resultado = new StringBuilder();
        int posicion = 1;

        for (JugadorRanking jugador : jugadoresTop) {
            resultado.append(posicion++)
                    .append(". ")
                    .append(jugador.getNombre())
                    .append(" - ")
                    .append(jugador.getRondasGanadas())
                    .append(" rondas ganadas\n");
        }
        if (resultado.toString().length() < 1)
            return "El top está vacío"; // cambiar
        return resultado.toString();
    }
}
