package br.com.infox.ibpm.bean;

import br.com.infox.ibpm.type.PrazoEnum;

public class PrazoTask {

	private Integer prazo;
	private PrazoEnum tipoPrazo;
	
	public void setPrazo(Integer prazo) {
		this.prazo = prazo;
	}
	
	public Integer getPrazo() {
		return prazo;
	}
	
	public void setTipoPrazo(PrazoEnum tipoPrazo) {
		this.tipoPrazo = tipoPrazo;
	}
	
	public PrazoEnum getTipoPrazo() {
		return tipoPrazo;
	}
}