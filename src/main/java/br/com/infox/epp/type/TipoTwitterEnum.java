package br.com.infox.epp.type;

import br.com.infox.type.Displayable;

public enum TipoTwitterEnum implements Displayable {

	U("Usuário"), L("Localização"), S("Usuário do Sistema");
	
	private String label;
	
	TipoTwitterEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}
	
}
