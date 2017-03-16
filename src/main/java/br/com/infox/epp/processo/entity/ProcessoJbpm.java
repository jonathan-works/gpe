package br.com.infox.epp.processo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Immutable;
import org.jbpm.graph.exe.ProcessInstance;

@Entity
@Table(name =  "tb_processo_jbpm", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_processo", "id_process_instance"})})
@Immutable
public class ProcessoJbpm implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_processo", nullable = false, insertable = false, updatable = false)
    private Processo processo;
    
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_process_instance", nullable = false, insertable = false, updatable = false, unique = true)
    private ProcessInstance processInstance;

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public ProcessInstance getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstance processInstance) {
        this.processInstance = processInstance;
    }
}
