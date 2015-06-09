package br.com.infox.epp.processo.prioridade.action;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;

@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Name(PrioridadeProcessoChanger.NAME)
@Transactional
public class PrioridadeProcessoChanger implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "prioridadeProcessoChanger";
	
	@In
    private ProcessoManager processoManager;
	@In
	private ActionMessagesService actionMessagesService;
	
    private Processo processo;
    private PrioridadeProcesso prioridadeProcesso;

    public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
        if (processo.getPrioridadeProcesso() != null) {
            this.prioridadeProcesso = processo.getPrioridadeProcesso();
        } else {
            this.prioridadeProcesso = new PrioridadeProcesso();
        }
	}
	
	public void setProcesso(Integer idProcesso) {
		Processo processo = processoManager.find(idProcesso);
		setProcesso(processo);
	}

    public PrioridadeProcesso getPrioridadeProcesso() {
        return prioridadeProcesso;
    }

    public void setPrioridadeProcesso(PrioridadeProcesso prioridadeProcesso) {
        this.prioridadeProcesso = prioridadeProcesso;
    }
    
    public void atualizarPrioridade() {
        processo.setPrioridadeProcesso(getPrioridadeProcesso());
        try {
        	processoManager.update(processo);
        } catch (DAOException e) {
        	actionMessagesService.handleDAOException(e);
        }
    }

}
