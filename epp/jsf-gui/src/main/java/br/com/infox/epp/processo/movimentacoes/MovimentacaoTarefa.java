package br.com.infox.epp.processo.movimentacoes;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Immutable;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "jbpm_taskinstance")
@Immutable
public class MovimentacaoTarefa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_", nullable = false, insertable = false, updatable = false)
    private Long id;
    @Column(name = "name_", nullable = false, insertable = false, updatable = false)
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_", nullable = false, insertable = false, updatable = false)
    private Date create;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_", nullable = false, insertable = false, updatable = false)
    private Date start;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_", nullable = false, insertable = false, updatable = false)
    private Date end;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "tb_processo_jbpm",
        joinColumns = @JoinColumn(
                name = "id_process_instance", referencedColumnName = "PROCINST_", 
                nullable = false, insertable = false, updatable = false),
        inverseJoinColumns=@JoinColumn(
                name = "id_processo", 
                nullable = false, insertable = false, updatable = false)
    )
    private List<Processo> processos;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROCINST_", nullable = false, insertable = false, updatable = false)
    private ProcessInstance processInstance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_", referencedColumnName = "ID_TASKINSTANCE", nullable = false, insertable = false, updatable = false)
    private UsuarioTaskInstance usuarioTaskInstance;

}
