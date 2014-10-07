package br.com.infox.epp.processo.type;

import br.com.infox.core.type.Displayable;

public enum TipoProcesso implements Displayable{
	
	PE("Processo EPA"), PD("Processo Documento");
	
	private String label;
	
	private TipoProcesso(String label){
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

}
