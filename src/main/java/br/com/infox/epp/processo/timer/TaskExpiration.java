
package br.com.infox.epp.processo.timer;

import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.GET_BY_FLUXO_AND_TASKNAME;
import static br.com.infox.epp.processo.timer.query.TaskExpirationQuery.GET_BY_FLUXO_AND_TASKNAME_QUERY;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import br.com.infox.epp.fluxo.entity.Fluxo;

@Entity
@Table(name = TaskExpiration.NAME)
@NamedQueries(value = {
        @NamedQuery(name = GET_BY_FLUXO_AND_TASKNAME, query = GET_BY_FLUXO_AND_TASKNAME_QUERY)
})
public class TaskExpiration implements Serializable {
    private static final long serialVersionUID = 1L;
    
    static final String NAME = "tb_task_expiration";
    private static final String GENERATOR_NAME = "TaskExpirationGenerator";
    private static final String SEQUENCE_NAME = "sq_task_expiration";
    
    @Id
    @SequenceGenerator(allocationSize = 1, initialValue = 1, name=GENERATOR_NAME, sequenceName = SEQUENCE_NAME)
    @GeneratedValue(generator = GENERATOR_NAME, strategy = GenerationType.SEQUENCE)
    @Column(name = "id_task_expiration", nullable = false, unique = true)
    private Integer id;
    
    @NotNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fluxo", nullable = false)
    private Fluxo fluxo;
    
    @NotNull
    @Column(name = "nm_tarefa", nullable = false)
    private String tarefa;
    
    @NotNull
    @Column(name = "ds_transition", nullable = false)
    private String transition;
    
    @NotNull
    @Column(name = "dt_expiration", nullable = false)
    private Date expiration;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Fluxo getFluxo() {
        return fluxo;
    }
    
    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
    }
    
    public String getTarefa() {
        return tarefa;
    }

    public void setTarefa(String tarefa) {
        this.tarefa = tarefa;
    }

    public String getTransition() {
        return transition;
    }
    
    public void setTransition(String transition) {
        this.transition = transition;
    }
    
    public Date getExpiration() {
        return expiration;
    }
    
    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}