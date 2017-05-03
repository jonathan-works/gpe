package br.com.infox.epp.fluxo.definicaovariavel;

import java.io.Serializable;
import java.util.List;

import br.com.infox.epp.fluxo.definicaovariavel.DefinicaoVariavelProcessoRecursos.RecursoVariavel;

public class DraggableListItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private RecursoVariavel recurso;
	private List<DefinicaoVariavelProcessoRecursoBean2> variaveis;
	private String id;

	public RecursoVariavel getRecurso() {
		return recurso;
	}

	public void setRecurso(RecursoVariavel recurso) {
		this.recurso = recurso;
	}

	public List<DefinicaoVariavelProcessoRecursoBean2> getVariaveis() {
		return variaveis;
	}

	public void setVariaveis(List<DefinicaoVariavelProcessoRecursoBean2> variaveis) {
		this.variaveis = variaveis;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
