package br.com.infox.epp.tarefa.entity;

import static br.com.infox.epp.tarefa.query.TarefaJbpmQuery.INSERT_TAREFA_VERSIONS;
import static br.com.infox.epp.tarefa.query.TarefaJbpmQuery.INSERT_TAREFA_VERSIONS_QUERY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = TarefaJbpm.TABLE_NAME, uniqueConstraints = { @UniqueConstraint(columnNames = {
    "id_tarefa", "id_jbpm_task" }) })
@NamedNativeQueries({ @NamedNativeQuery(name = INSERT_TAREFA_VERSIONS, query = INSERT_TAREFA_VERSIONS_QUERY) })
public class TarefaJbpm implements java.io.Serializable {

    public static final String TABLE_NAME = "tb_tarefa_jbpm";

    private static final long serialVersionUID = 1L;

    private int idTarefaJbpm;
    private Tarefa tarefa;
    private Long idJbpmTask;

    @SequenceGenerator(name = "generator", sequenceName = "sq_tb_tarefa_jbpm")
    @Id
    @GeneratedValue(generator = "generator", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_tarefa_jbpm", unique = true, nullable = false)
    public int getIdTarefaJbpm() {
        return idTarefaJbpm;
    }

    public void setIdTarefaJbpm(int idTarefaJbpm) {
        this.idTarefaJbpm = idTarefaJbpm;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarefa", nullable = false)
    @NotNull
    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    @Column(name = "id_jbpm_task", nullable = false)
    @NotNull
    public Long getIdJbpmTask() {
        return idJbpmTask;
    }

    public void setIdJbpmTask(Long idJbpmTask) {
        this.idJbpmTask = idJbpmTask;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TarefaJbpm)) {
            return false;
        }
        TarefaJbpm other = (TarefaJbpm) obj;
        if (getIdTarefaJbpm() != other.getIdTarefaJbpm()) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getIdTarefaJbpm();
        return result;
    }

}
