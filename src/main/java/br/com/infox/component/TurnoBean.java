package br.com.infox.component;

import java.sql.Time;

import br.com.infox.epp.type.DiaSemanaEnum;

public class TurnoBean {
	
	private DiaSemanaEnum diaSemana;
	private Time horaInicial;
	private Time horaFinal;
	
	public TurnoBean(DiaSemanaEnum diaSemana, Time horaInicial, Time horaFinal) {
		this.diaSemana = diaSemana;
		this.horaInicial = horaInicial;
		this.horaFinal = horaFinal;
	}
	
	public DiaSemanaEnum getDiaSemana() {
		return diaSemana;
	}
	
	public void setDiaSemana(DiaSemanaEnum diaSemana) {
		this.diaSemana = diaSemana;
	}
	
	public Time getHoraInicial() {
		return horaInicial;
	}
	
	public void setHoraInicial(Time horaInicial) {
		this.horaInicial = horaInicial;
	}
	
	public Time getHoraFinal() {
		return horaFinal;
	}
	
	public void setHoraFinal(Time horaFinal) {
		this.horaFinal = horaFinal;
	}
	
}