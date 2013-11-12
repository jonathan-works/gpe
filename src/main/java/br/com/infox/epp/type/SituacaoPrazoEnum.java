package br.com.infox.epp.type;

import br.com.infox.type.Displayable;

public enum SituacaoPrazoEnum implements Displayable {
	
	PAT("Processo Atrasado"), TAT("Tarefa Atrasada"), SAT("Sem atraso");
	
	private String label;
	
	SituacaoPrazoEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

}
