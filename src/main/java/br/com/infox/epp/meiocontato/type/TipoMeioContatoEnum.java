package br.com.infox.epp.meiocontato.type;

public enum TipoMeioContatoEnum {
	
	FA("Fax"), TF("Telefone Fixo"), TM("Telefone Móvel"), EM("Email");
	
	private String label;
	
	private TipoMeioContatoEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}

}
