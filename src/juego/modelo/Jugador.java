package juego.modelo;

import javax.swing.plaf.PanelUI;
import java.io.Serializable;
import java.util.Random;

public class Jugador implements Serializable {
    //crea un contador secreto y compartido para la clase Jugador para que asigne un número unico
    // a cada jugador al momento de crearse, y asegura que ese contador no se guarde
    transient private static int lastGeneratedID = 0;

    private int id;
    private Mano mano = new Mano();
    private int puntos = 0;
    private String nombre;
    private boolean listaParaJugar = false;

    public Jugador (String nombre){
        this.nombre = nombre;
        this.id = Jugador.lastGeneratedID;
    }

    public void tomarCartaMazo(Mazo mazoIn){
        this.mano.anadirCarta(mazoIn.tomarCartaTope());
    }

    public void descartarCarta(int cartaATirar, PilaCarta pilaDescarte){
        this.mano.transferirCarta(cartaATirar, pilaDescarte);
    }

    public void tomarCartaPilaDescarte (PilaCarta pilaDescarteIn){
        Carta cartaRecienTomada = pilaDescarteIn.tomarCartaTope();
        this.mano.anadirCarta(cartaRecienTomada);
    }

    public Mano getMano(){
        return this.mano;
    }

    public String getNombre(){
        return this.nombre;
    }

    public int getId(){
        return this.id;
    }

    public void setListoParaJugar (boolean listo){
        this.listaParaJugar = listo;
    }

    public boolean getListoParaJugar(){
        return this.listaParaJugar;
    }

    protected void resetMano(){
        this.mano = new Mano();
    }

    public int getPuntos(){
        return puntos;
    }

    public void añadirPuntosMano(){
        this.puntos = this.puntos + this.mano.calcularPuntajeRestante();
    }

    public static boolean validarNombre (String nombre){
        if (nombre==null || nombre.equals(""))
            return false;
        return true;
    }

    public static String generarNombreAleatorio() {
        String[] adjetivos = {
                "misterioso", "chistoso", "veloz", "gigante", "supremo", "bromista",
                "invencible", "radiante", "silencioso", "destructor", "mortal",
                "imparable", "explosivo", "demente", "fantasma", "inmortal", "invisible",
                "Celestial", "cósmico", "sabio", "infalible", "sagrado","Letal"
        };

        String[] nombresFamosos = {
                "Iron Man", "Capitán América", "Wonder Woman", "Hulk", "Thor", "Groot",
                "Flash", "Deadpool", "Loki", "Thanos", "Joker", "Venom", "Doctor Strange",
                "Black Widow", "Aquaman", "Ant-Man", "Magneto", "Doom", "Wolverine", "Ciclope"
        };

        Random rand = new Random();
        String nombreBaseAleatorio = nombresFamosos[rand.nextInt(nombresFamosos.length)];
        String adjetivoAleatorio = adjetivos[rand.nextInt(adjetivos.length)];

        return nombreBaseAleatorio + " " + adjetivoAleatorio;
    }

}
