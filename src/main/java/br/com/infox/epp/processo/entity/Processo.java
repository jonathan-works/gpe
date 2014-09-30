package br.com.infox.epp.processo.entity;

import static br.com.infox.core.constants.LengthConstants.NUMERACAO_PROCESSO;
import static br.com.infox.core.persistence.ORConstants.GENERATOR;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_ACTOR_ID_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_TODOS_OS_ACTOR_IDS;
import static br.com.infox.epp.processo.query.ProcessoQuery.ANULA_TODOS_OS_ACTOR_IDS_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.APAGA_ACTOR_ID_DO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.APAGA_ACTOR_ID_DO_PROCESSO_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS;
import static br.com.infox.epp.processo.query.ProcessoQuery.ATUALIZAR_PROCESSOS_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.DATA_FIM;
import static br.com.infox.epp.processo.query.ProcessoQuery.DATA_INICIO;
import static br.com.infox.epp.processo.query.ProcessoQuery.DURACAO;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST;
import static br.com.infox.epp.processo.query.ProcessoQuery.GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_JBPM;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.ID_USUARIO_CADASTRO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSOS_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSOS_PARA_CAIXA_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSO_PARA_CAIXA;
import static br.com.infox.epp.processo.query.ProcessoQuery.MOVER_PROCESSO_PARA_CAIXA_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.NOME_ACTOR_ID;
import static br.com.infox.epp.processo.query.ProcessoQuery.NUMERO_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.PROCESSO_ATTRIBUTE;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVER_PROCESSO_JBMP_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVE_PROCESSO_DA_CAIXA_ATUAL;
import static br.com.infox.epp.processo.query.ProcessoQuery.REMOVE_PROCESSO_DA_CAIXA_ATUAL_QUERY;
import static br.com.infox.epp.processo.query.ProcessoQuery.SEQUENCE_PROCESSO;
import static br.com.infox.epp.processo.query.ProcessoQuery.TABLE_PROCESSO;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.InheritanceType.JOINED;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Entity
@Table(name = TABLE_PROCESSO)
@Inheritance(strategy = JOINED)
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = APAGA_ACTOR_ID_DO_PROCESSO, query = APAGA_ACTOR_ID_DO_PROCESSO_QUERY),
    @NamedNativeQuery(name = REMOVE_PROCESSO_DA_CAIXA_ATUAL, query = REMOVE_PROCESSO_DA_CAIXA_ATUAL_QUERY),
    @NamedNativeQuery(name = ATUALIZAR_PROCESSOS, query = ATUALIZAR_PROCESSOS_QUERY),
    @NamedNativeQuery(name = ANULA_TODOS_OS_ACTOR_IDS, query = ANULA_TODOS_OS_ACTOR_IDS_QUERY),
    @NamedNativeQuery(name = MOVER_PROCESSO_PARA_CAIXA, query = MOVER_PROCESSO_PARA_CAIXA_QUERY),
    @NamedNativeQuery(name = ANULA_ACTOR_ID, query = ANULA_ACTOR_ID_QUERY),
    @NamedNativeQuery(name = REMOVER_PROCESSO_JBMP, query = REMOVER_PROCESSO_JBMP_QUERY),
    @NamedNativeQuery(name = GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST, query = GET_ID_TASKMGMINSTANCE_AND_ID_TOKEN_BY_PROCINST_QUERY)})
@NamedQueries(value = {
    @NamedQuery(name = LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID, query = LIST_PROCESSOS_BY_ID_PROCESSO_AND_ACTOR_ID_QUERY),
    @NamedQuery(name = MOVER_PROCESSOS_PARA_CAIXA, query = MOVER_PROCESSOS_PARA_CAIXA_QUERY) })
public class Processo implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idProcesso;
    private UsuarioLogin usuarioCadastroProcesso;
    private String numeroProcesso;
    private Date dataInicio;
    private Date dataFim;
    private Long duracao;
    private Caixa caixa;

    private Long idJbpm;

    private List<ProcessoDocumento> processoDocumentoList = new ArrayList<ProcessoDocumento>(0);

    private String actorId;

    public Processo() {
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = GENERATOR, sequenceName = SEQUENCE_PROCESSO)
    @Id
    @GeneratedValue(generator = GENERATOR, strategy = GenerationType.SEQUENCE)
    @Column(name = ID_PROCESSO, unique = true, nullable = false)
    public Integer getIdProcesso() {
        return this.idProcesso;
    }

    public void setIdProcesso(Integer idProcesso) {
        this.idProcesso = idProcesso;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_USUARIO_CADASTRO_PROCESSO)
    public UsuarioLogin getUsuarioCadastroProcesso() {
        return this.usuarioCadastroProcesso;
    }

    public void setUsuarioCadastroProcesso(UsuarioLogin usuarioCadastroProcesso) {
        this.usuarioCadastroProcesso = usuarioCadastroProcesso;
    }

    @Column(name = NUMERO_PROCESSO, nullable = false, length = NUMERACAO_PROCESSO)
    @NotNull
    @Size(max = NUMERACAO_PROCESSO)
    public String getNumeroProcesso() {
        return this.numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    @Temporal(TIMESTAMP)
    @Column(name = DATA_INICIO, nullable = false)
    @NotNull
    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    @Temporal(TIMESTAMP)
    @Column(name = DATA_FIM)
    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        if (dataFim != null && dataInicio != null) {
            setDuracao(dataFim.getTime() - dataInicio.getTime());
        }
        this.dataFim = dataFim;
    }

    @Column(name = DURACAO)
    public Long getDuracao() {
        return duracao;
    }

    public void setDuracao(Long duracao) {
        this.duracao = duracao;
    }

    @OneToMany(fetch = LAZY, mappedBy = PROCESSO_ATTRIBUTE, cascade={CascadeType.REMOVE})
    @OrderBy("dataInclusao DESC")
    public List<ProcessoDocumento> getProcessoDocumentoList() {
        return this.processoDocumentoList;
    }

    public void setProcessoDocumentoList(
            List<ProcessoDocumento> processoDocumentoList) {
        this.processoDocumentoList = processoDocumentoList;
    }

    @Override
    public String toString() {
        return numeroProcesso;
    }

    @Column(name = ID_JBPM)
    public Long getIdJbpm() {
        return idJbpm;
    }

    public void setIdJbpm(Long idJbpm) {
        this.idJbpm = idJbpm;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    @Column(name = NOME_ACTOR_ID)
    public String getActorId() {
        return actorId;
    }

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = ID_CAIXA)
    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((idProcesso == null) ? 0 : idProcesso.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Processo other = (Processo) obj;
        if (idProcesso == null) {
            if (other.idProcesso != null) {
                return false;
            }
        } else if (!idProcesso.equals(other.idProcesso)) {
            return false;
        }
        return true;
    }

}
