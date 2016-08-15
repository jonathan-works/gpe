package br.com.infox.epp.processo.comunicacao.tipo.crud;

import br.com.infox.core.type.Displayable;

public enum TipoUsoComunicacaoEnum implements Displayable {
	I("Interna"),
	E("Externa"),
	A("Ambos");

	private String label;
	
	TipoUsoComunicacaoEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

}
