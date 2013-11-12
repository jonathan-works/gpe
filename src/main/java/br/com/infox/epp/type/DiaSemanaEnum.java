package br.com.infox.epp.type;

import java.util.Arrays;

import br.com.infox.type.Displayable;

public enum DiaSemanaEnum implements Displayable {
	DOM("Domingo"), SEG("Segunda"), TER("Terça"), QUA("Quarta"), 
	QUI("Quinta"), SEX("Sexta"), SAB("Sábado");
	
	private String label;
	
	DiaSemanaEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	public int getNrDiaSemana() {
		return Arrays.asList(DiaSemanaEnum.values()).indexOf(this);
	}
}
