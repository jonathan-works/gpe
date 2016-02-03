package br.com.infox.epp.processo.localizacao.entity;

import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.COUNT_PROCESSO_LOCALIZACAO_IBPM_BY_ATTRIBUTES;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.COUNT_PROCESSO_LOC_IBPM_BY_IDP_LOC_AND_PAPEL_QUERY;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.DELETE_BY_PROCESS_ID_AND_TASK_ID;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.DELETE_BY_PROCESS_ID_AND_TASK_ID_QUERY;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.DELETE_BY_TASK_INSTANCE_ID;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.DELETE_BY_TASK_INSTANCE_ID_QUERY;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.LIST_BY_TASK_INSTANCE;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.LIST_BY_TASK_INSTANCE_QUERY;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL;
import static br.com.infox.epp.processo.localizacao.query.ProcessoLocalizacaoIbpmQuery.LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL_QUERY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.system.annotation.Ignore;

@Entity
@Ignore
@Table(name = ProcessoLocalizacaoIbpm.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = LIST_BY_TASK_INSTANCE, query = LIST_BY_TASK_INSTANCE_QUERY),
    @NamedQuery(name = COUNT_PROCESSO_LOCALIZACAO_IBPM_BY_ATTRIBUTES, query = COUNT_PROCESSO_LOC_IBPM_BY_IDP_LOC_AND_PAPEL_QUERY),
    @NamedQuery(name = DELETE_BY_PROCESS_ID_AND_TASK_ID, query = DELETE_BY_PROCESS_ID_AND_TASK_ID_QUERY),
    @NamedQuery(name = LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL, query = LIST_ID_TASK_INSTANCE_BY_LOCALIZACAO_PAPEL_QUERY),
    @NamedQuery(name = DELETE_BY_TASK_INSTANCE_ID, query = DELETE_BY_TASK_INSTANCE_ID_QUERY)
})
public class ProcessoLocalizacaoIbpm implements java.io.Serializable {

    public static final String TABLE_NAME = "tb_processo_localizacao_ibpm";

    private static final long serialVersionUID = 1L;

    private int idProcessoLocalizacaoIbpm;
    private Processo processo;
    private Long idProcessInstanceJbpm;
    private Localizacao localizacao;
    private Papel papel;
    private Long idTaskJbpm;
    private boolean contabilizar;
    private Long idTaskInstance;

    public ProcessoLocalizacaoIbpm() {
    }

    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_processo_localizacao_ibpm")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_processo_localizacao", unique = true, nullable = false)
    public int getIdProcessoLocalizacaoIbpm() {
        return idProcessoLocalizacaoIbpm;
    }

    public void setIdProcessoLocalizacaoIbpm(int idProcessoLocalizacaoIbpm) {
        this.idProcessoLocalizacaoIbpm = idProcessoLocalizacaoIbpm;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_processo", nullable = false, updatable = false)
    @NotNull
    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    @Column(name = "id_processinstance_jbpm")
    public Long getIdProcessInstanceJbpm() {
        return idProcessInstanceJbpm;
    }

    public void setIdProcessInstanceJbpm(Long idProcessInstanceJbpm) {
        this.idProcessInstanceJbpm = idProcessInstanceJbpm;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_localizacao", nullable = false)
    @NotNull
    public Localizacao getLocalizacao() {
        return this.localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_papel")
    public Papel getPapel() {
        return papel;
    }

    public void setPapel(Papel papel) {
        this.papel = papel;
    }

    @Column(name = "id_task_jbpm", nullable = false)
    @NotNull
    public Long getIdTaskJbpm() {
        return idTaskJbpm;
    }

    public void setIdTaskJbpm(Long idTaskJbpm) {
        this.idTaskJbpm = idTaskJbpm;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ProcessoLocalizacaoIbpm)) {
            return false;
        }
        ProcessoLocalizacaoIbpm other = (ProcessoLocalizacaoIbpm) obj;
        if (getIdProcessoLocalizacaoIbpm() != other.getIdProcessoLocalizacaoIbpm()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdProcessoLocalizacaoIbpm();
        return result;
    }

    public void setContabilizar(boolean contabilizar) {
        this.contabilizar = contabilizar;
    }

    @Column(name = "in_contabilizar", nullable = false)
    @NotNull
    public boolean getContabilizar() {
        return contabilizar;
    }

    public void setIdTaskInstance(Long idTaskInstance) {
        this.idTaskInstance = idTaskInstance;
    }

    @Column(name = "id_task_instance", nullable = false)
    @NotNull
    public Long getIdTaskInstance() {
        return idTaskInstance;
    }
}
