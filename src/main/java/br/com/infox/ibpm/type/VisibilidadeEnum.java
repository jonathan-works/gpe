package br.com.infox.ibpm.type;

import br.com.infox.type.Displayable;


public enum VisibilidadeEnum implements Displayable {

	A("Ambos"), I("Interno"), E("Externo");
	
	private String label;
	
	VisibilidadeEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}

}