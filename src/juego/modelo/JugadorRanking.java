package juego.modelo;

import java.io.Serializable;

public class JugadorRanking implements Serializable {
    private String nombre;
    private int rondasGanadas;
    public JugadorRanking(String nombre, int rondasGanadas){
        this.nombre=nombre;
        this.rondasGanadas=rondasGanadas;
    }

    public int getRondasGanadas(){
        return this.rondasGanadas;
    }

    public void aumentarRondasGanadas(){
        this.rondasGanadas++;
    }

    public String getNombre(){
        return this.nombre;
    }
}
