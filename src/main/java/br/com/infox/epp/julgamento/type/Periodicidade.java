package br.com.infox.epp.julgamento.type;

import br.com.infox.core.type.Displayable;

public enum Periodicidade implements Displayable{
	
	D("Diariamente"), S("Semanalmente"), M("Mensalmente"), A("Anualmente");
	
	private String label;
	
	private Periodicidade(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

}
