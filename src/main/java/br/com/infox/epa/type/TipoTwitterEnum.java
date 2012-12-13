package br.com.infox.epa.type;

public enum TipoTwitterEnum {

	U("Usuário"), L("Localização"), S("Usuário do Sistema");
	
	private String label;
	
	TipoTwitterEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
}
