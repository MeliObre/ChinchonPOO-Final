package juego.modelo;

public class Carta {
    private final Palo palo;
    private final int numero;


    public Carta(Palo palo, int numero){
        this.palo=palo;
        this.numero=numero;
    }
    public Palo getPalo(){
        return palo;
    }
    public int getNumero(){
        return numero;
    }

    @Override
    public String toString(){
        return this.numero + " de " +this.palo;
    }

}
