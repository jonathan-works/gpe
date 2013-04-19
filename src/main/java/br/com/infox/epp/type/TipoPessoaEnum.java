package br.com.infox.epp.type;

public enum TipoPessoaEnum {

	F("Física"), J("Jurídica");
	
	private String label;
	
	TipoPessoaEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
}
