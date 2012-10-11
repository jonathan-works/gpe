package br.com.infox.epa.type;

import java.util.Arrays;

public enum DiaSemanaEnum {
	DOM("Domingo"), SEG("Segunda"), TER("Ter�a"), QUA("Quarta"), 
	QUI("Quinta"), SEX("Sexta"), SAB("S�bado");
	
	private String label;
	
	DiaSemanaEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public int getNrDiaSemana() {
		return Arrays.asList(DiaSemanaEnum.values()).indexOf(this);
	}
	

}
