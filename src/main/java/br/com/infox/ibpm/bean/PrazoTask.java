package br.com.infox.ibpm.bean;

import java.io.Serializable;

import br.com.infox.ibpm.type.PrazoEnum;

public class PrazoTask implements Serializable {

	private static final long serialVersionUID = 5863339056482857365L;
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