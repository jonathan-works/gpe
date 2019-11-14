package br.com.infox.epp.processo.documento.type;

import br.com.infox.core.type.Displayable;

public enum TipoAlteracaoDocumento implements Displayable{
	
	E("Exclusão"), R("Restauração");
	
	private String label;
	
	private TipoAlteracaoDocumento(String label){
		this.label = label;
	}
	
	@Override
	public String getLabel(){
		return label;
	}

}
