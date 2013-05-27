package br.com.infox.ibpm.type;

public enum TipoNumeracaoEnum {
	S("Sequencial"), J("Classe Java");
	
	private String label;
	
	TipoNumeracaoEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
}
