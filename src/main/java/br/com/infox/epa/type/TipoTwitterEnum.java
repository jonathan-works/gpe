package br.com.infox.epa.type;

public enum TipoTwitterEnum {

	U("Usu�rio"), L("Localiza��o");
	
	private String label;
	
	TipoTwitterEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
}
