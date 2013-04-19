package br.com.infox.epp.type;

public enum TipoTwitterEnum {

	U("Usu�rio"), L("Localiza��o"), S("Usu�rio do Sistema");
	
	private String label;
	
	TipoTwitterEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}
	
}
