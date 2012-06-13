package br.com.infox.epa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.validator.NotNull;

import br.com.infox.epa.query.ProcessoEpaQuery;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.Processo;

@Entity
@Table(name=ProcessoEpa.NAME, schema="public")
@PrimaryKeyJoinColumn
@NamedQueries(value={
		@NamedQuery(name=ProcessoEpaQuery.LIST_ALL_NOT_ENDED,
					query=ProcessoEpaQuery.LIST_ALL_NOT_ENDED_QUERY)
})
public class ProcessoEpa extends Processo {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tb_processo_epa";

	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private Localizacao localizacao;
	private Integer tempoGasto;
	private Integer porcentagem;
	
	private List<ProcessoEpaTarefa> processoEpaTarefaList = new ArrayList<ProcessoEpaTarefa>();
	
	public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
		this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_natureza_categoria_fluxo", nullable=false)
	@NotNull
	public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
		return naturezaCategoriaFluxo;
	}
	
	public void setLocalizacao(Localizacao localizacao) {
		this.localizacao = localizacao;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_localizacao", nullable=false)
	@NotNull
	public Localizacao getLocalizacao() {
		return localizacao;
	}

	public void setProcessoEpaTarefaList(List<ProcessoEpaTarefa> processoEpaTarefaList) {
		this.processoEpaTarefaList = processoEpaTarefaList;
	}

	@OneToMany(mappedBy="processoEpa", fetch=FetchType.LAZY)
	public List<ProcessoEpaTarefa> getProcessoEpaTarefaList() {
		return processoEpaTarefaList;
	}

	public void setTempoGasto(Integer tempoGasto) {
		this.tempoGasto = tempoGasto;
	}

	@Column(name="nr_tempo_gasto")
	public Integer getTempoGasto() {
		return tempoGasto;
	}

	public void setPorcentagem(Integer porcentagem) {
		this.porcentagem = porcentagem;
	}

	@Column(name="nr_porcentagem")
	public Integer getPorcentagem() {
		return porcentagem;
	}
	
}