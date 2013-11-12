package br.com.infox.epp.type;

import br.com.infox.type.Displayable;

public enum TipoPessoaEnum implements Displayable {

	F("Física"), J("Jurídica");
	
	private String label;
	
	TipoPessoaEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
}
