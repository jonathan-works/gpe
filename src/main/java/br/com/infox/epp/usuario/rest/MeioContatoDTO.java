package br.com.infox.epp.usuario.rest;

import br.com.infox.epp.meiocontato.entity.MeioContato;

public class MeioContatoDTO {

	private String tipo;
	private String meioContato;

	public MeioContatoDTO() {
	}

	public MeioContatoDTO(MeioContato meioContato) {
		this.meioContato = meioContato.getMeioContato();
		this.tipo = meioContato.getTipoMeioContato().name();
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getMeioContato() {
		return meioContato;
	}

	public void setMeioContato(String meioContato) {
		this.meioContato = meioContato;
	}

}
