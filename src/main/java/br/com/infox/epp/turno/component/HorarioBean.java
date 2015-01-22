package br.com.infox.epp.turno.component;

import java.util.Date;

public class HorarioBean {
    private Date hora;
    private boolean selected;

    public Date getHora() {
        return this.hora;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void setHora(final Date hora) {
        this.hora = hora;
    }

    public void setSelected(final boolean selected) {
        this.selected = selected;
    }
}
