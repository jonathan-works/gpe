package br.com.infox.ibpm.type;

public enum PrazoEnum {

	H("Hora(s)"), D("Dia(s)");
	
	private String label;
	
	PrazoEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
}