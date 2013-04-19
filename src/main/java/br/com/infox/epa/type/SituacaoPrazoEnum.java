package br.com.infox.epa.type;

public enum SituacaoPrazoEnum {
	
	PAT("Processo Atrasado"), TAT("Tarefa Atrasada"), SAT("Sem atraso");
	
	String label;
	
	SituacaoPrazoEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

}
