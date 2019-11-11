package br.com.infox.epp.system.custom.variables;

import br.com.infox.core.type.Displayable;

public enum TipoCustomVariableEnum implements Displayable{
	BOOLEAN("Booleano"),
	DATE("Data"),
	DOUBLE("Número decimal"),
	LONG("Número inteiro"),
	STRING("Texto"),
	EL("Expressão")
	;

	private String label;
	
	TipoCustomVariableEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}
	
	

}
