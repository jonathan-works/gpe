package br.com.infox.epp.turno.component;

import java.sql.Time;

public class HorarioBean {
	private Time hora;
	private boolean selected;
	
	public boolean getSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Time getHora() {
		return hora;
	}
	public void setHora(Time hora) {
		this.hora = hora;
	}
}