package juego.vista.pseudoconsola;

import juego.enumerados.EstadoPrograma;
import juego.modelo.Carta;
import juego.modelo.Jugador;
import juego.modelo.Mano;
import juego.modelo.Palo;
import juego.utilidades.MetodosUtiles;
import juego.vista.base.VistaBase;
import juego.controlador.Controlador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

public class Pseudoconsola extends VistaBase {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField edit_input;
    private JTextArea memo_display;
    private JButton butt_enter;
    private JTextArea display_mano_y_pila;
    private EstadoPrograma estadoActual = EstadoPrograma.ESPERANDO_LISTO_PARA_JUGAR;

    public Pseudoconsola(Controlador controlador) {
        super(controlador);
        setFont(new Font("Monospaced", Font.PLAIN, 12));
        setTitle("Chinchón :-)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 733, 424);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 10, 540, 319);
        contentPane.add(scrollPane);

        this.memo_display = new JTextArea();
        scrollPane.setViewportView(memo_display);
        memo_display.setEditable(false);
        memo_display.setFont(new Font("Monospaced", Font.PLAIN, 16));

        this.edit_input = new JTextField();
        edit_input.setFont(new Font("Monospaced", Font.PLAIN, 16));
        edit_input.setBounds(10, 339, 540, 38);
        contentPane.add(edit_input);
        edit_input.setColumns(10);

        this.butt_enter = new JButton("Enter");
        butt_enter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    enterPresionado();
                } catch (RemoteException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                limpiarBarraComandos();
            }
        });
        butt_enter.setBounds(560, 339, 149, 38);
        contentPane.add(butt_enter);

        display_mano_y_pila = new JTextArea();
        display_mano_y_pila.setFont(new Font("Monospaced", Font.PLAIN, 14));
        display_mano_y_pila.setBounds(560, 10, 149, 319);
        contentPane.add(display_mano_y_pila);
        display_mano_y_pila.setEditable(false);
        display_mano_y_pila.setLineWrap(true);
    }

    @Override
    public void bloquear() throws RemoteException {
        this.setEstadoActual(EstadoPrograma.ESPERANDO_TURNO);
        this.mostrarMenu(false, true);
    }

    @Override
    public void actualizarManoYPila() throws RemoteException {
        this.mostrarManoYPila();
    }

    @Override
    public void tomarDeMazoOPila() throws RemoteException {
        this.setEstadoActual(EstadoPrograma.ELIGIENDO_MAZO_O_PILA);
        this.mostrarMenu(false, true);
    }

    @Override
    public void descartarOCerrar() throws RemoteException {
        this.setEstadoActual(EstadoPrograma.DESCARTAR_O_CERRAR);
        this.mostrarMenu(false, true);
    }

    public void mostrarPuntos() throws RemoteException {
        this.println("El jugador [" + this.getJugadorActual().getNombre() + "] ha cerrado la ronda.");
        Jugador[] jugadores = this.getJugadores();
        for (Jugador jugador : jugadores) {
            this.println("Jugador : [" + jugador.getNombre() + "]");
            this.println("Puntos: " + jugador.getPuntos());
        }
    }

    public void perder() throws RemoteException {
        setEstadoActual(EstadoPrograma.PERDIO);
        this.mostrarMenu(false, true);
    }

    public void ganar() throws RemoteException {
        this.setEstadoActual(EstadoPrograma.GANO);
        this.mostrarMenu(false, true);
    }

    private void setEstadoActual(EstadoPrograma nuevoEstado) {
        this.estadoActual = nuevoEstado;
    }

    private void enterPresionado() throws RemoteException {
        // si presionan enter, manejamos el input del usuario
        this.clear();
        if (inputConsola().equalsIgnoreCase("top")) {
            this.println(this.getJugadoresTopString());
            mostrarMenu(false, false);
            return;
        }

        switch ((EstadoPrograma) this.estadoActual) {
            default:
                mostrarMenu(false, true);
                break;

            case ESPERANDO_LISTO_PARA_JUGAR:
                this.cambiarListoParaJugar(inputConsola());
                break;

            case DESCARTAR_O_CERRAR:
                this.elegirDescarte(inputConsola());
                break;

            case REGISTRANDO_JUGADOR:
                break;

            case ELIGIENDO_MAZO_O_PILA:
                this.eligiendoMazoOPila(inputConsola());
                break;

            case ESPERANDO_TURNO:
                println("Está jugando el jugador [" + this.getJugadorActual().getNombre() + "].");
                println("Por favor, espere su turno.");
                // Esto se vuelve a escribir por el clear que se hace de forma incondicional
                break;
        }

    }

    private void elegirDescarte(String cartaDescartar) throws RemoteException {
        if (!(MetodosUtiles.esInt(cartaDescartar))) {
            this.println("Posición inválida. Ingrese la posición de su carta a descartar.");
            return;
        }

        int nCartaDescartar = Integer.valueOf(cartaDescartar);

        if (nCartaDescartar == 0) {
            // si quiere descartar la carta 0, se interpreta como que quiere cerrar
            this.terminarRonda();
            return;
        }

        if (nCartaDescartar < 0 || nCartaDescartar > getCantidadCartas()) {
            this.println("Posición inválida. Ingrese la posición de su carta a descartar.");
            return;
        }

        this.descartar(nCartaDescartar);
    }

    private String inputConsola() {
        return this.edit_input.getText().trim();
    }

    private int getCantidadCartas() throws RemoteException {
        return this.getJugador().getMano().getCantidadCartas();
    }

    private void cambiarListoParaJugar(String opcion) throws RemoteException {
        this.clear();
        switch ((String) opcion) {
            case "1":
                this.println("Usted está listo para jugar.");
                this.setListoParaJugar(true);
                break;
            case "2":
                this.println("Usted no está listo para jugar.");
                this.setListoParaJugar(false);
                break;

            default:
                this.mostrarMenu(true, true);
                break;
        }
    }

    public void println(String textoAMostrar) {
        this.memo_display.append(textoAMostrar + "\n");
    }

    public void println(int numeroAMostrar) {
        this.println("" + numeroAMostrar);
    }

    public void println() {
        this.println("");
    }

    private void print(String textoAMostrar) {
        this.memo_display.setText(this.memo_display.getText() + textoAMostrar);
    }

    private void mostrarCarta(Carta carta) {
        this.print(this.cartaAString(carta));
    }

    private void mostrarManoYPila() throws RemoteException {
        this.display_mano_y_pila
                .setText("Pila: \n" + this.cartaAString(this.getTopePila()) + "\n" + "Su mano:");
        this.verMiMano();
    }

    private void verMiMano() throws RemoteException {
        this.display_mano_y_pila.setText(
                this.display_mano_y_pila.getText() + "\n" + manoAString(this.getJugador().getMano()));
    }

    private String manoAString(Mano mano) {
        String textoSalida = "";
        for (int i = 1; i <= mano.getCantidadCartas(); i++) {
            textoSalida = textoSalida + String.valueOf(i) + ". ";
            textoSalida = textoSalida + cartaAString(mano.getCarta(i));
            textoSalida = textoSalida + "\n";
        }
        return textoSalida;
    }

    private String cartaAString(Carta carta) {
        if (carta == null) {
            return "[NO HAY CARTA/S]";
        }
        if (carta.getPalo() == Palo.COMODIN) {
            return "[COMODÍN]";
        }
        String textoSalida = "[" + String.valueOf(carta.getNumero()) + " de " + carta.getPalo() + "]";
        return textoSalida;
    }

    private void clear() {
        this.memo_display.setText("");
    }

    private void limpiarBarraComandos() {
        this.edit_input.setText("");
    }

    private boolean puedeCerrarRonda() throws RemoteException {
        return this.getMano().esCerrable();
    }

    private void mostrarMenu(boolean mostrarError, boolean limpiar) throws RemoteException {
        if (limpiar)
            this.clear();

        if (mostrarError)
            println("Hubo un problema. Sus opciones son:");

        switch (this.estadoActual) {
            case ESPERANDO_LISTO_PARA_JUGAR:
                this.println("Opciones:");
                this.println("1. Prepararse para jugar.");
                break;

            case ESPERANDO_TURNO:
                this.println("Está jugando el jugador [" + this.getJugadorActual().getNombre() + "].");
                this.println("Por favor, espere su turno.");
                break;

            case ELIGIENDO_MAZO_O_PILA:
                this.println("1. Tomar carta del mazo.");
                this.println("2. Tomar carta de la pila de descarte.");
                break;

            case DESCARTAR_O_CERRAR:
                this.println("Ingrese la posición de la carta que desea descartar.");
                if (puedeCerrarRonda())
                    this.println("También puede ingresar 0 si desea cerrar la ronda.");
                break;

            case REGISTRANDO_JUGADOR:
                this.println("Ingrese su nombre de jugador:");
                break;

            case GANO:
                this.println("¡GANASTE!");
                break;

            case PERDIO:
                this.println("¡PERDISTE!");
                break;

            default:
                this.println("Usted está en estado de: " + this.estadoActual);
                break;
        }
    }

    private void eligiendoMazoOPila(String opcion) throws RemoteException {
        if (opcion.equals("1")) {
            this.tomarTopeMazo();
        } else if (opcion.equals("2")) {
            this.tomarTopePilaDescarte();
        } else {
            this.mostrarMenu(true, true);

        }
        this.mostrarManoYPila();
    }

    @Override
    public void esperarNuevaRonda() throws RemoteException {
        this.setEstadoActual(EstadoPrograma.ESPERANDO_LISTO_PARA_JUGAR);
        // this.botonAccion.setText("Listo para jugar");
        this.mostrarMenu(false, true);
    }

    @Override
    public void logicaInicioAdicional() {
        // no hacer nada xD
    }

    @Override
    public void sesionIniciada() throws RemoteException {
        setTitle("Chinchon 2D: " + this.getJugador().getNombre() + ": 0 puntos");
        setVisible(true);
        this.mostrarMenu(false, true);
    }



}
