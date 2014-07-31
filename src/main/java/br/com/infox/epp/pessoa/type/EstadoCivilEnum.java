package br.com.infox.epp.pessoa.type;

public enum EstadoCivilEnum {

	S("Solteiro"),
	C("Casado"),
	V("Viúvo"),
	D("Divorciado"),
	J("Sep Judicialmente"),
	U("União Estável"),
	N("Não Informado");
	
	private EstadoCivilEnum(String label) {
		this.label = label;
	}
	
	private String label;
	
	public String getLabel() {
		return this.label;
	}
}
