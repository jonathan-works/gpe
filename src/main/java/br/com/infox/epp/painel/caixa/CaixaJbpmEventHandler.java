package br.com.infox.epp.painel.caixa;

import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.bpm.cdi.qualifier.Events.TaskEnd;
import br.com.infox.bpm.cdi.qualifier.Events.Transition;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;

@Stateless
public class CaixaJbpmEventHandler {

    @Inject
    private ProcessoManager processoManager;
    @Inject
    private CaixaManager caixaManager;

    public void moverProcessoParaCaixaDestino(@Observes @Transition ExecutionContext executionContext) throws DAOException {
    	Node nodeTo = executionContext.getTransition().getTo();
    	Node nodeFrom = executionContext.getTransition().getFrom();
	    Caixa caixa = caixaManager.getCaixaByDestinationNodeKeyNodeAnterior(nodeTo.getKey(), nodeFrom.getKey());
	    if (caixa != null) {
	        Processo processo = processoManager.getProcessoEpaByIdJbpm(executionContext.getProcessInstance().getId());
	        if (caixa != null) {
	            processo.setCaixa(caixa);
	            processoManager.update(processo);
	        }
	    }
    }
    
   public void removeCaixaDoProcesso(@Observes @TaskEnd ExecutionContext context) throws DAOException {
       Processo processo = processoManager.getProcessoEpaByIdJbpm(context.getProcessInstance().getRoot().getId());
       processo.setCaixa(null);
       processoManager.update(processo);
   }

}
