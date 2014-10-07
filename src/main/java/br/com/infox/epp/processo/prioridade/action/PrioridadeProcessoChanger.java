package br.com.infox.epp.processo.prioridade.action;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.ProcessoEpa;
import br.com.infox.epp.processo.manager.ProcessoEpaManager;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(PrioridadeProcessoChanger.NAME)
public class PrioridadeProcessoChanger implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "prioridadeProcessoChanger";

    private ProcessoEpa processoEpa;
    private PrioridadeProcesso prioridadeProcesso;

    @In
    private ProcessoEpaManager processoEpaManager;

    public ProcessoEpa getProcessoEpa() {
        return processoEpa;
    }

    public void setProcessoEpa(ProcessoEpa instance) {
        this.processoEpa = instance;
        if (instance.getPrioridadeProcesso() != null) {
            this.prioridadeProcesso = instance.getPrioridadeProcesso();
        } else {
            this.prioridadeProcesso = new PrioridadeProcesso();
        }
    }

    public PrioridadeProcesso getPrioridadeProcesso() {
        return prioridadeProcesso;
    }

    public void setPrioridadeProcesso(PrioridadeProcesso prioridadeProcesso) {
        this.prioridadeProcesso = prioridadeProcesso;
    }
    
    public void atualizarPrioridade() {
        processoEpa.setPrioridadeProcesso(getPrioridadeProcesso());
        try {
            processoEpaManager.update(processoEpa);
        } catch (DAOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
