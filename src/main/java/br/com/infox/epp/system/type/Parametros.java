package br.com.infox.epp.system.type;

public enum Parametros {
	
	ID_STATUS_PROC_ELABORACAO("idStatusProcessoEmElaboracao"),
	ID_STATUS_PROC_ENVIADO("idStatusProcessoEnviado");
	
	private String label;
	
	private Parametros(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return this.label;
	}

}
