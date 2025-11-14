package juego.modelo;
import java.io.Serializable;

public class ResultCierre {
    private static final long serialVersionUID = 1L;

    public final boolean esCerrable;
    public final Carta cartaCorte;
    public final Mano mano;

    public ResultCierre (boolean esCerrable, Carta cartaCorte, Mano mano){
        this.esCerrable= esCerrable;
        this.cartaCorte= cartaCorte;
        this.mano= mano;
    }

}
