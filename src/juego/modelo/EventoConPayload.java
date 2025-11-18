package juego.modelo;

import java.io.Serializable;
import juego.enumerados.Evento;

public class EventoConPayload implements Serializable {
    private Evento evento;
    private int datoNumerico;
    private boolean esValidoElDatoNumerico = false;
    private Object objetoPayload = null;
    private Class<?> claseDelPayload = null;

    public EventoConPayload(Evento evento, int datoNumerico, Object objetoPayload) {
        this.evento = evento;
        this.datoNumerico = datoNumerico;
        this.esValidoElDatoNumerico = true;
        this.objetoPayload = objetoPayload;
        if (objetoPayload != null) {
            this.claseDelPayload = objetoPayload.getClass();
        }
    }

    public EventoConPayload(Evento evento, int datoNumerico) {
        this.evento = evento;
        this.datoNumerico = datoNumerico;
        this.esValidoElDatoNumerico = true;
    }

    public EventoConPayload(Evento evento, Object objetoPayload) {
        this.evento = evento;
        this.objetoPayload = objetoPayload;
        if (objetoPayload != null) {
            this.claseDelPayload = objetoPayload.getClass();
        }
    }

    public EventoConPayload(Evento evento) {
        this.evento = evento;
    }

    public Evento getEvento() {
        return evento;
    }

    public int getDatoNumerico() {
        if (!esValidoElDatoNumerico) {
            throw new IllegalStateException("El dato numerico no es valido para este evento");
        }
        return datoNumerico;
    }

    public boolean esValidoElDatoNumerico() {
        return esValidoElDatoNumerico;
    }

    public Object getObjetoPayload() {
        return objetoPayload;
    }

    public Class<?> getClaseDelPayload() {
        return claseDelPayload;
    }

    public boolean esPayloadDeTipo(Class<?> tipoEsperado) {
        return claseDelPayload != null && tipoEsperado.isAssignableFrom(claseDelPayload);
    }
}
