package br.com.infox.epp.type;

import java.util.Arrays;

public enum DiaSemanaEnum {
	DOM("Domingo"), SEG("Segunda"), TER("Terça"), QUA("Quarta"), 
	QUI("Quinta"), SEX("Sexta"), SAB("Sábado");
	
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
