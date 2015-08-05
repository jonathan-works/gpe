package br.com.infox.epp.painel.caixa;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.util.JbpmUtil;

@Scope(ScopeType.EVENT)
@Name(CaixaJbpmEventHandler.NAME)
public class CaixaJbpmEventHandler implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "caixaJbpmEventHandler";

    @In
    private ProcessoManager processoManager;
    @In
    private CaixaManager caixaManager;
    @In
    private TarefaManager tarefaManager; 

    @Observer(Event.EVENTTYPE_TRANSITION)
    public void moverProcessoParaCaixaDestino(ExecutionContext executionContext) throws DAOException {
    	Transition transition = executionContext.getTransition();
    	String nomeTarefaDestino = transition.getTo().getName();
    	String nomeFluxo = transition.getTo().getProcessDefinition().getName();
        Tarefa tarefa = tarefaManager.getTarefa(nomeTarefaDestino, nomeFluxo);
        if (tarefa != null) {
	        Integer idNodeAnterior = (int) transition.getFrom().getId();
	        Caixa caixa = caixaManager.getCaixaByIdTarefaAndIdNodeAnterior(tarefa.getIdTarefa(), idNodeAnterior);
	        Processo processo = processoManager.getProcessoEpaByIdJbpm(executionContext.getProcessInstance().getId());
	        if (caixa != null) {
	        	processo.setCaixa(caixa);
	        	processoManager.update(processo);
	        }
        }
    }
    
   @Observer(Event.EVENTTYPE_TASK_END)
   public void removeCaixaDoProcesso(ExecutionContext context) throws DAOException {
       Processo processo = processoManager.getProcessoEpaByIdJbpm(context.getProcessInstance().getId());
       processo.setCaixa(null);
       processoManager.update(processo);
   }

}
