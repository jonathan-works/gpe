package br.com.infox.epp.usuario.rest;

import javax.validation.constraints.NotNull;

import br.com.infox.epp.meiocontato.entity.MeioContato;

public class MeioContatoDTO {

	@NotNull
	private String tipo;
	@NotNull
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
