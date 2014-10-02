package br.com.infox.epp.processo.entity;

import static br.com.infox.epp.processo.query.ProcessoEpaQuery.COUNT_PARTES_ATIVAS_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.COUNT_PARTES_ATIVAS_DO_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.DATA_INICIO_PRIMEIRA_TAREFA;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.DATA_INICIO_PRIMEIRA_TAREFA_QUERY;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.ITEM_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.ITEM_DO_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.LIST_ALL_NOT_ENDED;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.LIST_ALL_NOT_ENDED_QUERY;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.LIST_NOT_ENDED_BY_FLUXO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.LIST_NOT_ENDED_BY_FLUXO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.PROCESSO_EPA_BY_ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.PROCESSO_EPA_BY_ID_JBPM_QUERY;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.TEMPO_GASTO_PROCESSO_EPP;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.TEMPO_GASTO_PROCESSO_EPP_QUERY;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO;
import static br.com.infox.epp.processo.query.ProcessoEpaQuery.TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_PROCESSO_BY_NUMERO_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_ATTRIBUTE;
import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.partes.entity.ParteProcesso;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

@Entity
@Table(name = ProcessoEpa.TABLE_NAME)
@DiscriminatorValue(value = "PE")
@PrimaryKeyJoinColumn
@NamedQueries(value = {
    @NamedQuery(name = LIST_ALL_NOT_ENDED, query = LIST_ALL_NOT_ENDED_QUERY),
    @NamedQuery(name = PROCESSO_EPA_BY_ID_JBPM, query = PROCESSO_EPA_BY_ID_JBPM_QUERY),
    @NamedQuery(name = COUNT_PARTES_ATIVAS_DO_PROCESSO, query = COUNT_PARTES_ATIVAS_DO_PROCESSO_QUERY),
    @NamedQuery(name = ITEM_DO_PROCESSO, query = ITEM_DO_PROCESSO_QUERY),
    @NamedQuery(name = LIST_NOT_ENDED_BY_FLUXO, query = LIST_NOT_ENDED_BY_FLUXO_QUERY),
    @NamedQuery(name = DATA_INICIO_PRIMEIRA_TAREFA, query = DATA_INICIO_PRIMEIRA_TAREFA_QUERY),
    @NamedQuery(name = TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO, query = TEMPO_MEDIO_PROCESSO_BY_FLUXO_AND_SITUACAO_QUERY),
    @NamedQuery(name = TEMPO_GASTO_PROCESSO_EPP, query = TEMPO_GASTO_PROCESSO_EPP_QUERY),
    @NamedQuery(name = GET_PROCESSO_BY_NUMERO_PROCESSO, query = GET_PROCESSO_BY_NUMERO_PROCESSO_QUERY) 
})
public class ProcessoEpa extends Processo {

    private static final long serialVersionUID = 1L;
    public static final String TABLE_NAME = "tb_processo_epa";

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_natureza_categoria_fluxo", nullable = false)
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_localizacao", nullable = false)
    private Localizacao localizacao;
    
    @Column(name = "nr_tempo_gasto")
    private Integer tempoGasto;
    
    @Column(name = "nr_porcentagem")
    private Integer porcentagem;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_item_processo")
    private Item itemDoProcesso;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "st_prazo", nullable = false)
    private SituacaoPrazoEnum situacaoPrazo;
    
    @NotNull
    @Column(name = "in_contabilizar", nullable = false)
    private Boolean contabilizar = Boolean.TRUE;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_prioridade_processo", nullable = true)
    private PrioridadeProcesso prioridadeProcesso;
    
    @OneToMany(mappedBy = "processoEpa", fetch = FetchType.LAZY)
    private List<ProcessoEpaTarefa> processoEpaTarefaList = new ArrayList<ProcessoEpaTarefa>(0);
    
    @OneToMany(mappedBy = "processo", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE })
    private List<ParteProcesso> partes = new ArrayList<ParteProcesso>(0);
    
    @OneToMany(fetch = LAZY, mappedBy = PROCESSO_ATTRIBUTE, cascade={CascadeType.REMOVE})
    @OrderBy("dataInclusao DESC")
    private List<Documento> processoDocumentoList = new ArrayList<Documento>(0);
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uni_decisora_monocratica", nullable = true)
    private UnidadeDecisoraMonocratica decisoraMonocratica;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uni_decisora_colegiada", nullable = true)
    private UnidadeDecisoraColegiada decisoraColegiada;
    
    public ProcessoEpa() {
        super();
    }

    public ProcessoEpa(final SituacaoPrazoEnum situacaoPrazo,
            final Date dataInicio, final String numeroProcesso,
            final UsuarioLogin usuarioLogado,
            final NaturezaCategoriaFluxo naturezaCategoriaFluxo,
            final Localizacao localizacao, final Item itemDoProcesso) {
        this.setSituacaoPrazo(situacaoPrazo);
        this.setDataInicio(dataInicio);
        this.setNumeroProcesso(numeroProcesso);
        this.setUsuarioCadastroProcesso(usuarioLogado);
        this.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
        this.setLocalizacao(localizacao);
        this.setItemDoProcesso(itemDoProcesso);
    }

    public void setNaturezaCategoriaFluxo(NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
    }

    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
        return naturezaCategoriaFluxo;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setProcessoEpaTarefaList(
            List<ProcessoEpaTarefa> processoEpaTarefaList) {
        this.processoEpaTarefaList = processoEpaTarefaList;
    }

    public List<ProcessoEpaTarefa> getProcessoEpaTarefaList() {
        return processoEpaTarefaList;
    }

    public void setTempoGasto(Integer tempoGasto) {
        this.tempoGasto = tempoGasto;
    }

    public Integer getTempoGasto() {
        return tempoGasto;
    }

    public void setPorcentagem(Integer porcentagem) {
        this.porcentagem = porcentagem;
    }

    public Integer getPorcentagem() {
        return porcentagem;
    }

    public Item getItemDoProcesso() {
        return itemDoProcesso;
    }

    public void setItemDoProcesso(Item itemDoProcesso) {
        this.itemDoProcesso = itemDoProcesso;
    }

    public SituacaoPrazoEnum getSituacaoPrazo() {
        return situacaoPrazo;
    }

    public void setSituacaoPrazo(SituacaoPrazoEnum situacaoPrazo) {
        this.situacaoPrazo = situacaoPrazo;
    }

    public Boolean getContabilizar() {
        return contabilizar;
    }

    public void setContabilizar(Boolean contabilizar) {
        this.contabilizar = contabilizar;
    }

    public PrioridadeProcesso getPrioridadeProcesso() {
        return prioridadeProcesso;
    }

    public void setPrioridadeProcesso(PrioridadeProcesso prioridadeProcesso) {
        this.prioridadeProcesso = prioridadeProcesso;
    }

    public boolean hasPartes() {
        return naturezaCategoriaFluxo.getNatureza().getHasPartes();
    }

    public List<ParteProcesso> getPartes() {
        return partes;
    }

    public void setPartes(List<ParteProcesso> partes) {
        this.partes = partes;
    }

    public UnidadeDecisoraMonocratica getDecisoraMonocratica() {
        return decisoraMonocratica;
    }

    public void setDecisoraMonocratica(UnidadeDecisoraMonocratica decisoraMonocratica) {
        this.decisoraMonocratica = decisoraMonocratica;
    }

    public UnidadeDecisoraColegiada getDecisoraColegiada() {
        return decisoraColegiada;
    }

    public void setDecisoraColegiada(UnidadeDecisoraColegiada decisoraColegiada) {
        this.decisoraColegiada = decisoraColegiada;
    }
}
