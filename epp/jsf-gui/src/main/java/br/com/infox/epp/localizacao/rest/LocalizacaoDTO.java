package br.com.infox.epp.localizacao.rest;

import br.com.infox.epp.access.entity.Localizacao;

public class LocalizacaoDTO {

	private String nome;
	private String codigo;
	private String codigoLocalizacaoSuperior;
	private String codigoEstrutura;

	public LocalizacaoDTO() {
	}

	public LocalizacaoDTO(Localizacao localizacao){
		this(localizacao.getLocalizacao(), localizacao.getCodigo(), localizacao.getLocalizacaoPai() == null ? null : localizacao.getLocalizacaoPai().getCodigo(), localizacao.getEstruturaFilho() == null ? null : localizacao.getEstruturaFilho().getNome());
	}
	
	public LocalizacaoDTO(String nome, String codigo, String codigoLocalizacaoSuperior, String nomeEstrutura) {
		this.nome = nome;
		this.codigo = codigo;
		this.codigoLocalizacaoSuperior = codigoLocalizacaoSuperior;
		this.codigoEstrutura = nomeEstrutura;
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

	public String getCodigoLocalizacaoSuperior() {
		return codigoLocalizacaoSuperior;
	}

	public void setCodigoLocalizacaoSuperior(String codigoLocalizacaoSuperior) {
		this.codigoLocalizacaoSuperior = codigoLocalizacaoSuperior;
	}

	public String getCodigoEstrutura() {
		return codigoEstrutura;
	}

	public void setCodigoEstrutura(String codigoEstrutura) {
		this.codigoEstrutura = codigoEstrutura;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result + ((codigoLocalizacaoSuperior == null) ? 0 : codigoLocalizacaoSuperior.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocalizacaoDTO other = (LocalizacaoDTO) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (codigoLocalizacaoSuperior == null) {
			if (other.codigoLocalizacaoSuperior != null)
				return false;
		} else if (!codigoLocalizacaoSuperior.equals(other.codigoLocalizacaoSuperior))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getNome();
	}
	
}
