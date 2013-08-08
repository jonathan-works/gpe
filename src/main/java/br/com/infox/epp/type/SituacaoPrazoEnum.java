package br.com.infox.epp.type;

public enum SituacaoPrazoEnum {
	
	PAT("Processo Atrasado"), TAT("Tarefa Atrasada"), SAT("Sem atraso");
	
	private String label;
	
	SituacaoPrazoEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

}
