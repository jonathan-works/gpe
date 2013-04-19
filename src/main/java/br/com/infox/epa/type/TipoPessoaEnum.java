package br.com.infox.epa.type;

public enum TipoPessoaEnum {

	F("F�sica"), J("Jur�dica");
	
	private String label;
	
	TipoPessoaEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
}
