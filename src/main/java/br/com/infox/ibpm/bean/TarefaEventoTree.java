package br.com.infox.ibpm.bean;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Event;

import br.com.infox.epp.manager.AgrupamentoManager;
import br.com.infox.epp.manager.ProcessoTarefaEventoManager;
import br.com.infox.epp.manager.TarefaEventoManager;
import br.com.infox.ibpm.component.tree.AutomaticEventsTreeHandler;
import br.com.infox.ibpm.entity.Agrupamento;
import br.com.infox.ibpm.entity.TarefaEvento;
import br.com.infox.ibpm.type.TarefaEventoEnum;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.EntityUtil;

@Name(value = TarefaEventoTree.NAME)
@Scope(ScopeType.CONVERSATION)
public class TarefaEventoTree implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final LogProvider LOG = Logging.getLogProvider(TarefaEventoTree.class);
	
	public static final String NAME = "tarefaEventoTree";
	
	private String agrupamentos;
	private boolean canLeave;
	private TarefaEvento currentEvent;
	
	@In private AgrupamentoManager agrupamentoManager;
	@In private TarefaEventoManager tarefaEventoManager;
	@In private ProcessoTarefaEventoManager processoTarefaEventoManager;
	
	public static TarefaEventoTree instance(){
		return (TarefaEventoTree) Component.getInstance(TarefaEventoTree.NAME);
	}
	
	/**
	 * Verifica se existe alguem Agrupamento para ser registrado naquela tarefa
	 * e atribui ao currentEvent o tipo do evento corrente.
	 */
	public String getAgrupamentos() {
		if(agrupamentos == null) {
			StringBuilder agrupamentosId = new StringBuilder();
			currentEvent = tarefaEventoManager.getNextTarefaEvento();
			int i = 0;
			List<Agrupamento> agrupamentos = agrupamentoManager.getAgrupamentosByTarefaEvento(currentEvent);
			for(Agrupamento agrupamento : agrupamentos) {
				if(i != 0) {
					agrupamentosId.append(", ");
				}
				agrupamentosId.append(agrupamento.getIdAgrupamento());
				i++;
			}
			this.agrupamentos = agrupamentosId.toString();
		}
		return agrupamentos; 
	}

	@Observer(AutomaticEventsTreeHandler.AFTER_REGISTER_EVENT)
	/**
	 * Observer para o método que registra os eventos, pois é necessário verificar
	 * se existe mais algum evento na mesma tarefa para remontar a tree e efetuar
	 * a regra de negócio necessária.
	 */
	public void afterRegisterEvent() {
		if(currentEvent != null) {
			processoTarefaEventoManager.marcarProcessoTarefaEventoComoRegistrado(currentEvent);
			currentEvent = null;
			canLeave = processoTarefaEventoManager.existemEventosNaoRegistrados();
			if(!canLeave) {
				agrupamentos = null;
				AutomaticEventsTreeHandler.instance().getRoots(getAgrupamentos());
				AutomaticEventsTreeHandler.instance().setRegistred(false);
				if(currentEvent.getEvento().equals(TarefaEventoEnum.ST)) {
					canLeave = true;
				}
			}
		}
	}
	
	/**
	 * Método invocado quando finalmente o processo sai da tarefa, apagando assim
	 * os registros de ProcessoTarefaEvento referente aquele processo, pois caso
	 * este mesmo retorne para esta tarefa, deve ser possível registrar todos os
	 * eventos novamente.
	 */
	@Observer(Event.EVENTTYPE_TASK_END)
	public void onLeaveTask() {
		try {
			processoTarefaEventoManager.destroyProcessoTarefaEvento();
		} catch (Exception ex) {
			String action = "deletar da tabela processoTarefaEvento os eventos finalizados";
			LOG.warn(action, ex);
			throw new AplicationException(AplicationException.
					createMessage(action+ex.getLocalizedMessage(), 
								  "onLeaveTask()", 
								  "TarefaEventoTreeHandler", 
								  "BPM"));
		}
	}
	
	public TarefaEvento getCurrentEvent() {
		return currentEvent;
	}

	public void setCurrentEvent(TarefaEvento currentEvent) {
		this.currentEvent = currentEvent;
	}
	
	public Boolean getCanLeave() {
		return canLeave;
	}
	
	public void setCanLeave(Boolean canLeave) {
		this.canLeave = canLeave;
	}
	
	public static EntityManager getEntityManager() {
		return EntityUtil.getEntityManager();
	}

}
