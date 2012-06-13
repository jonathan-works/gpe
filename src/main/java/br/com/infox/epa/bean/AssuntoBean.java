package br.com.infox.epa.bean;

import br.com.infox.ibpm.entity.Assunto;

public class AssuntoBean {

	private Assunto assunto;
	private boolean checked;
	
	public AssuntoBean(Assunto assunto) {
		this.assunto = assunto;
	}

	public void setAssunto(Assunto assunto) {
		this.assunto = assunto;
	}
	
	public Assunto getAssunto() {
		return assunto;
	}
	
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public boolean isChecked() {
		return checked;
	}
	
}