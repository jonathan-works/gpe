package br.com.infox.ibpm.type;


public enum VisibilidadeEnum {

	A("Ambos"), I("Interno"), E("Externo");
	
	private String label;
	
	VisibilidadeEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}

}