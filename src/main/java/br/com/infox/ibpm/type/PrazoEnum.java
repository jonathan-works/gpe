package br.com.infox.ibpm.type;

import br.com.infox.core.type.Displayable;

public enum PrazoEnum implements Displayable {

	H("Hora(s)"), D("Dia(s)");
	
	private String label;
	
	PrazoEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
}