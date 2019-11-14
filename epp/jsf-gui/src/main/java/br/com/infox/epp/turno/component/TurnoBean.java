package br.com.infox.epp.turno.component;

import java.util.Date;

import br.com.infox.epp.turno.type.DiaSemanaEnum;

public class TurnoBean {

    private DiaSemanaEnum diaSemana;
    private Date horaInicial;
    private Date horaFinal;

    public TurnoBean(final DiaSemanaEnum diaSemana, final Date horaInicial,
            final Date horaFinal) {
        this.diaSemana = diaSemana;
        this.horaInicial = horaInicial;
        this.horaFinal = horaFinal;
    }

    public DiaSemanaEnum getDiaSemana() {
        return this.diaSemana;
    }

    public Date getHoraFinal() {
        return this.horaFinal;
    }

    public Date getHoraInicial() {
        return this.horaInicial;
    }

    public void setDiaSemana(final DiaSemanaEnum diaSemana) {
        this.diaSemana = diaSemana;
    }

    public void setHoraFinal(final Date horaFinal) {
        this.horaFinal = horaFinal;
    }

    public void setHoraInicial(final Date horaInicial) {
        this.horaInicial = horaInicial;
    }

}
