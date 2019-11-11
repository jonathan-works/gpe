package br.com.infox.epp.fluxo.definicaovariavel;

import java.io.Serializable;

import br.com.infox.epp.fluxo.entity.Fluxo;

public class DefinicaoVariavelProcessoBean2 implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String nome;
	private String label;
	private Fluxo fluxo;
	private String valorPadrao;
	private Long version = 0L;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}

	public String getValorPadrao() {
		return valorPadrao;
	}

	public void setValorPadrao(String valorPadrao) {
		this.valorPadrao = valorPadrao;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

}
