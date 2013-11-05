package br.com.infox.epp.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.query.ProcessoEpaQuery;
import br.com.infox.epp.type.SituacaoPrazoEnum;
import br.com.infox.ibpm.entity.Localizacao;
import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.infox.ibpm.entity.Processo;

@Entity
@Table(name=ProcessoEpa.TABLE_NAME, schema="public")
@PrimaryKeyJoinColumn
@NamedQueries(value={
		@NamedQuery(name=ProcessoEpaQuery.LIST_ALL_NOT_ENDED,
					query=ProcessoEpaQuery.LIST_ALL_NOT_ENDED_QUERY),
		@NamedQuery(name=ProcessoEpaQuery.LIST_NOT_ENDED_BY_FLUXO,
					query=ProcessoEpaQuery.LIST_NOT_ENDED_BY_FLUXO_QUERY)
})
public class ProcessoEpa extends Processo {

	private static final long serialVersionUID = 1L;
	public static final String TABLE_NAME = "tb_processo_epa";

	private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
	private Localizacao localizacao;
	private Integer tempoGasto;
	private Integer porcentagem;
	private Item itemDoProcesso;
	private SituacaoPrazoEnum situacaoPrazo;
	private Boolean contabilizar = Boolean.TRUE;
	
	private List<ProcessoEpaTarefa> processoEpaTarefaList = new ArrayList<ProcessoEpaTarefa>(0);
	private List<ParteProcesso> partes = new ArrayList<ParteProcesso>(0);
	
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

	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_item_processo", nullable=false)
	public Item getItemDoProcesso() {
		return itemDoProcesso;
	}

	public void setItemDoProcesso(Item itemDoProcesso) {
		this.itemDoProcesso = itemDoProcesso;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="st_prazo", nullable=false)
	@NotNull
	public SituacaoPrazoEnum getSituacaoPrazo() {
		return situacaoPrazo;
	}

	public void setSituacaoPrazo(SituacaoPrazoEnum situacaoPrazo) {
		this.situacaoPrazo = situacaoPrazo;
	}
	
	@Column(name="in_contabilizar", nullable=false)
	@NotNull
	public Boolean getContabilizar() {
		return contabilizar;
	}
	public void setContabilizar(Boolean contabilizar) {
		this.contabilizar = contabilizar;
	}

	public boolean hasPartes(){
		return naturezaCategoriaFluxo.getNatureza().getHasPartes();
	}

	@OneToMany(mappedBy="processo", fetch=FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	public List<ParteProcesso> getPartes() {
		return partes;
	}

	public void setPartes(List<ParteProcesso> partes) {
		this.partes = partes;
	}
	
	
}