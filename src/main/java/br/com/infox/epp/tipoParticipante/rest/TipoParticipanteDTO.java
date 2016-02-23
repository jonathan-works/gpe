package br.com.infox.epp.tipoParticipante.rest;

import br.com.infox.epp.processo.partes.entity.TipoParte;

public class TipoParticipanteDTO {
	private String nome;
	private String codigo;

	public TipoParticipanteDTO() {
	}
	
	public TipoParticipanteDTO(TipoParte tipoParte) {
		this.codigo = tipoParte.getIdentificador();
		this.nome = tipoParte.getDescricao();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}
