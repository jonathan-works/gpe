package br.com.infox.epp.tarefa.entity;

import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_ID_JBPM_TASK;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_ID_JBPM_TASK_QUERY;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_TAREFA_AND_FLUXO;
import static br.com.infox.epp.tarefa.query.TarefaQuery.TAREFA_BY_TAREFA_AND_FLUXO_QUERY;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.constants.LengthConstants;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.tarefa.type.PrazoEnum;

@Entity
@Table(name = Tarefa.TABLE_NAME)
@NamedQueries({
    @NamedQuery(name = TAREFA_BY_TAREFA_AND_FLUXO, query = TAREFA_BY_TAREFA_AND_FLUXO_QUERY),
    @NamedQuery(name = TAREFA_BY_ID_JBPM_TASK, query = TAREFA_BY_ID_JBPM_TASK_QUERY) })
public class Tarefa implements java.io.Serializable {

    public static final String TABLE_NAME = "tb_tarefa";

    private static final long serialVersionUID = 1L;

    private int idTarefa;
    private String tarefa;
    private Fluxo fluxo;
    private Integer prazo;
    private PrazoEnum tipoPrazo;

    private List<TarefaJbpm> tarefaJbpmList = new ArrayList<TarefaJbpm>(0);

    @SequenceGenerator(allocationSize=1, initialValue=1, name = "generator", sequenceName = "sq_tb_tarefa")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tarefa", unique = true, nullable = false)
    public int getIdTarefa() {
        return idTarefa;
    }

    public void setIdTarefa(int idTarefa) {
        this.idTarefa = idTarefa;
    }

    @Column(name = "ds_tarefa", nullable = false, length = LengthConstants.DESCRICAO_MEDIA, unique = true)
    @NotNull
    @Size(max = LengthConstants.DESCRICAO_MEDIA)
    public String getTarefa() {
        return tarefa;
    }

    public void setTarefa(String tarefa) {
        this.tarefa = tarefa;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fluxo", nullable = false)
    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tarefa")
    @OrderBy("idJbpmTask DESC")
    public List<TarefaJbpm> getTarefaJbpmList() {
        return tarefaJbpmList;
    }

    public void setTarefaJbpmList(List<TarefaJbpm> tarefaJbpmList) {
        this.tarefaJbpmList = tarefaJbpmList;
    }

    @Transient
    public Long getLastIdJbpmTask() {
        if (tarefaJbpmList == null || tarefaJbpmList.size() == 0) {
            return null;
        }
        return tarefaJbpmList.get(0).getIdJbpmTask();
    }

    @Override
    public String toString() {
        return tarefa;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tarefa)) {
            return false;
        }
        Tarefa other = (Tarefa) obj;
        if (getIdTarefa() != other.getIdTarefa()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdTarefa();
        return result;
    }

    public void setPrazo(Integer prazo) {
        this.prazo = prazo;
    }

    @Column(name = "nr_prazo")
    public Integer getPrazo() {
        return prazo;
    }

    public void setTipoPrazo(PrazoEnum tipoPrazo) {
        this.tipoPrazo = tipoPrazo;
    }

    @Column(name = "tp_prazo")
    @Enumerated(EnumType.STRING)
    public PrazoEnum getTipoPrazo() {
        return tipoPrazo;
    }
}
