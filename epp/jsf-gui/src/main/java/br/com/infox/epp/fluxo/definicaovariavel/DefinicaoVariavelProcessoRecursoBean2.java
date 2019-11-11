package br.com.infox.epp.fluxo.definicaovariavel;

import java.io.Serializable;

public class DefinicaoVariavelProcessoRecursoBean2 implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	//private DefinicaoVariavelProcesso definicaoVariavelProcesso;
	private String recurso;
	private Integer ordem;
	private Boolean visivelUsuarioExterno = false;
	private Long version;
	private DefinicaoVariavelProcessoBean2 definicao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/*public DefinicaoVariavelProcesso getDefinicaoVariavelProcesso() {
		return definicaoVariavelProcesso;
	}

	public void setDefinicaoVariavelProcesso(DefinicaoVariavelProcesso definicaoVariavelProcesso) {
		this.definicaoVariavelProcesso = definicaoVariavelProcesso;
	}*/

	public String getRecurso() {
		return recurso;
	}

	public void setRecurso(String recurso) {
		this.recurso = recurso;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Boolean getVisivelUsuarioExterno() {
		return visivelUsuarioExterno;
	}

	public void setVisivelUsuarioExterno(Boolean visivelUsuarioExterno) {
		this.visivelUsuarioExterno = visivelUsuarioExterno;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public DefinicaoVariavelProcessoBean2 getDefinicao() {
		return definicao;
	}

	public void setDefinicao(DefinicaoVariavelProcessoBean2 definicao) {
		this.definicao = definicao;
	}

}
