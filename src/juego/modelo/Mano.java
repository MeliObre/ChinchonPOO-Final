package juego.modelo;

import juego.servicios.ComprobarMano;

import java.util.List;

public class Mano extends ConjuntoCartas{

    public boolean esCerrable (){
        return ComprobarMano.esManoCerrable(this.getCopiaArrayList()).esCerrable;
    }

    public int cerrarMano (){
        ResultCierre resultadoCierre = ComprobarMano.esManoCerrable(this.getCopiaArrayList());
        if (!resultadoCierre.esCerrable)
            throw new Error("La mano es cerrable.");
        if (resultadoCierre.cartaCorte != null){
            this.quitarCarta(resultadoCierre.cartaCorte);
        }
        return calcularPuntajeRestante();
    }

    public int calcularPuntajeRestante(){
        if (esChinchon())
            return -1000;
        int puntosPorSobra = ComprobarMano.sumarCartasNoUsadas(this.getCopiaArrayList());
        if (puntosPorSobra == 0)
            return -10;
        return puntosPorSobra;
    }

    public boolean esChinchon(){
        this.ordenarCarta();
        Carta cartaAnterior = this.getCarta(1);
        for (int i = 2; i <= this.getCantidadCartas(); i++){
            Carta cartaActual = this.getCarta(i);

            if (cartaActual.getPalo() != cartaAnterior.getPalo())
                return false;

            if ((cartaAnterior.getNumero() + 1) != cartaActual.getNumero()){
                return false;
            }
            cartaAnterior = cartaActual;
        }

        return true;
    }

    public String toString (){
        String textoSalida= "";
        for (int i = 1; i <= this.getCantidadCartas(); i++){
            textoSalida = textoSalida+String.valueOf(i)+ " . ";
            textoSalida = textoSalida+ this.getCarta(i).toString();
            textoSalida = textoSalida + "\n";
        }
        return textoSalida;
    }

    public static Mano arrayToMano(List<Carta> cartas) {
        Mano resultado = new Mano();
        for (int i = 0; i < cartas.size(); i++) {
            resultado.anadirCarta(cartas.get(i));
        }
        return resultado;
    }
}
